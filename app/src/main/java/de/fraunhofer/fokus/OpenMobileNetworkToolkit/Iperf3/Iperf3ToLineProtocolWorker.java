/*
 *  SPDX-FileCopyrightText: 2023 Peter Hasse <peter.hasse@fokus.fraunhofer.de>
 *  SPDX-FileCopyrightText: 2023 Johann Hackler <johann.hackler@fokus.fraunhofer.de>
 *  SPDX-FileCopyrightText: 2023 Fraunhofer FOKUS
 *
 *  SPDX-License-Identifier: BSD-3-Clause-Clear
 */

package de.fraunhofer.fokus.OpenMobileNetworkToolkit.Iperf3;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.fraunhofer.fokus.OpenMobileNetworkToolkit.DataProvider.DeviceInformation;
import de.fraunhofer.fokus.OpenMobileNetworkToolkit.GlobalVars;
import de.fraunhofer.fokus.OpenMobileNetworkToolkit.InfluxDB2x.InfluxdbConnection;
import de.fraunhofer.fokus.OpenMobileNetworkToolkit.InfluxDB2x.InfluxdbConnections;
import de.fraunhofer.fokus.OpenMobileNetworkToolkit.Iperf3.JSON.Interval;
import de.fraunhofer.fokus.OpenMobileNetworkToolkit.Iperf3.JSON.Root;
import de.fraunhofer.fokus.OpenMobileNetworkToolkit.Iperf3.JSON.Stream;

public class Iperf3ToLineProtocolWorker extends Worker {
    private static final String TAG = "Iperf3UploadWorker";
    InfluxdbConnection influx;
    private SharedPreferences sp;
    private String rawIperf3file;
    private String measurementName;
    private String ip;

    private String port;
    private String bandwidth;
    private String duration;
    private String intervalIperf;
    private String bytes;
    private String protocol;
    private String iperf3LineProtocolFile;

    private DeviceInformation di = GlobalVars.getInstance().get_dp().getDeviceInformation();

    private boolean rev;
    private boolean biDir;
    private boolean oneOff;
    private boolean client;

    private String runID;
    public Iperf3ToLineProtocolWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        rawIperf3file = getInputData().getString("rawIperf3file");

        ip = getInputData().getString("ip");
        measurementName = getInputData().getString("measurementName");
        iperf3LineProtocolFile = getInputData().getString("iperf3LineProtocolFile");
        port = getInputData().getString("port");
        if(port == null)
            port = "5201";
        protocol = getInputData().getString("protocol");
        bandwidth = getInputData().getString("bandwidth");

        if(bandwidth == null){
            if(protocol.equals("TCP")) {
                bandwidth = "unlimited";
            } else {
                bandwidth = "1000";
            }
        }

        duration = getInputData().getString("duration");
        if(duration == null)
            duration = "10";
        intervalIperf = getInputData().getString("interval");
        if(intervalIperf == null)
            intervalIperf = "1";
        bytes = getInputData().getString("bytes");
        if(bytes == null){
            if(protocol.equals("TCP")) {
                bytes = "8";
            } else {
                bytes = "1470";
            }
        }

        rev = getInputData().getBoolean("rev", false);
        biDir = getInputData().getBoolean("biDir",false);
        oneOff = getInputData().getBoolean("oneOff",false);
        client = getInputData().getBoolean("client",false);
        runID = getInputData().getString("iperf3WorkerID");
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    private void setup(){
        influx = InfluxdbConnections.getRicInstance(getApplicationContext());
    }


    public Map<String, String> getTagsMap() {
        String tags = sp.getString("tags", "").strip().replace(" ", "");
        Map<String, String> tags_map = Collections.emptyMap();
        if (!tags.isEmpty()) {
            try {
                tags_map = Splitter.on(',').withKeyValueSeparator('=').split(tags);
            } catch (IllegalArgumentException e) {
                Log.d(TAG, "cant parse tags, ignoring");
            }
        }
        Map<String, String> tags_map_modifiable = new HashMap<>(tags_map);
        tags_map_modifiable.put("measurement_name", sp.getString("measurement_name", "OMNT"));
        tags_map_modifiable.put("manufacturer", di.getManufacturer());
        tags_map_modifiable.put("model", di.getModel());
        tags_map_modifiable.put("sdk_version", String.valueOf(di.getAndroidSDK()));
        tags_map_modifiable.put("android_version", di.getAndroidRelease());
        tags_map_modifiable.put("secruity_patch", di.getSecurityPatchLevel());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            tags_map_modifiable.put("soc_model", di.getSOCModel());
        }
        tags_map_modifiable.put("radio_version", Build.getRadioVersion());
        return tags_map_modifiable;
    }


    @NonNull
    @Override
    public Result doWork() {
        setup();
        Data output = new Data.Builder().putBoolean("iperf3_upload", false).build();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(rawIperf3file));
        } catch (FileNotFoundException | NullPointerException e) {
            e.printStackTrace();
            return Result.failure(output);
        }
        Root iperf3AsJson = new Gson().fromJson(br, Root.class);
        long timestamp = iperf3AsJson.start.timestamp.timesecs*1000;
        Log.d(TAG, "doWork: "+timestamp);

        String role = "server";
        if(iperf3AsJson.start.connectingTo != null){
            role = "client";
        }

        LinkedList<Point> points = new LinkedList<Point>();
        for (Interval interval: iperf3AsJson.intervals) {
            long tmpTimestamp = timestamp + (long) (interval.sum.end * 1000);
            int intervalIdx = iperf3AsJson.intervals.indexOf(interval);
            for (Stream stream: interval.streams){
                Point point = new Point("Iperf3");
                point.addTag("run_uid", runID);
                point.addTag("bidir", String.valueOf(biDir));
                point.addTag("sender", String.valueOf(stream.sender));
                point.addTag("role", role);
                point.addTag("socket", String.valueOf(stream.socket));
                point.addTag("protocol", protocol);
                point.addTag("interval", intervalIperf);
                point.addTag("version", iperf3AsJson.start.version);
                point.addTag("reversed", String.valueOf(rev));
                point.addTag("oneOff", String.valueOf(oneOff));
                point.addTag("connectingToHost", iperf3AsJson.start.connectingTo.host);
                point.addTag("connectingToPort", String.valueOf(iperf3AsJson.start.connectingTo.port));
                point.addTag("bandwith", bandwidth);
                point.addTag("duration", duration);
                point.addTag("bytesToTransmit", bytes);
                point.addTag("streams", String.valueOf(interval.streams.size()));
                point.addTag("streamIdx", String.valueOf(interval.streams.indexOf(stream)));
                point.addTag("intervalIdx", String.valueOf(intervalIdx));

                point.addField("bits_per_second", stream.bitsPerSecond);
                point.addField("seconds", stream.seconds);
                point.addField("bytes", stream.bytes);

                if(stream.rtt != 0) point.addField("rtt", stream.rtt);
                if(stream.rttvar != 0) point.addField("rttvar", stream.rttvar);
                if(stream.jitterMs != 0) point.addField("jitter_ms", stream.jitterMs);
                if(protocol.equals("UDP") && rev){
                    point.addField("lost_packets", stream.lostPackets);
                    point.addField("lost_percent", stream.lostPercent);
                }

                point.addField("retransmits", stream.retransmits);

                point.time(tmpTimestamp, WritePrecision.MS);

                points.add(point);
            }
        }

        // is needed when only --udp is, otherwise no lostpackets/lostpercent parsed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            for (Point point:points) {
                point.addTags(getTagsMap());
            }

        }


        FileOutputStream iperf3Stream = null;
        try {
            iperf3Stream = new FileOutputStream(iperf3LineProtocolFile, true);
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(), "logfile not created", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        if(iperf3Stream == null){
            Toast.makeText(getApplicationContext(),"FileOutputStream is not created for LP file", Toast.LENGTH_SHORT).show();
            return Result.failure();
        }

        try {
            for (Point point: points){
                iperf3Stream.write((point.toLineProtocol() + "\n").getBytes());
            }
        } catch (IOException e) {
            Log.e(TAG, "doWork: ", e);
        }


        output = new Data.Builder().putBoolean("iperf3_to_lp", true).build();
        return Result.success(output);
    }

}
