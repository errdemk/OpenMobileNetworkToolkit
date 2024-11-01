/*
 * SPDX-FileCopyrightText: 2024 Peter Hasse <peter.hasse@fokus.fraunhofer.de>
 *  SPDX-FileCopyrightText: 2024 Johann Hackler <johann.hackler@fokus.fraunhofer.de>
 * SPDX-FileCopyrightText: 2024 Fraunhofer FOKUS
 *
 * SPDX-License-Identifier: apache2
 */

package de.fraunhofer.fokus.OpenMobileNetworkToolkit.DataProvider;

import static android.net.wifi.ScanResult.CHANNEL_WIDTH_160MHZ;
import static android.net.wifi.ScanResult.CHANNEL_WIDTH_20MHZ;
import static android.net.wifi.ScanResult.CHANNEL_WIDTH_320MHZ;
import static android.net.wifi.ScanResult.CHANNEL_WIDTH_40MHZ;
import static android.net.wifi.ScanResult.CHANNEL_WIDTH_80MHZ;
import static android.net.wifi.ScanResult.CHANNEL_WIDTH_80MHZ_PLUS_MHZ;
import static android.net.wifi.ScanResult.WIFI_STANDARD_11AC;
import static android.net.wifi.ScanResult.WIFI_STANDARD_11AD;
import static android.net.wifi.ScanResult.WIFI_STANDARD_11AX;
import static android.net.wifi.ScanResult.WIFI_STANDARD_11BE;
import static android.net.wifi.ScanResult.WIFI_STANDARD_11N;
import static android.net.wifi.ScanResult.WIFI_STANDARD_LEGACY;
import static android.net.wifi.ScanResult.WIFI_STANDARD_UNKNOWN;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Build;

import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

public class WifiInformation extends Information {
    private String ssid;
    private String bssid;
    private int rssi;
    private int frequency;
    private int link_speed;
    private int tx_link_speed;
    private int max_tx_link_speed;
    private int rx_link_speed;
    private int max_rx_link_speed;
    private int standard;
    private int channel_bandwidth;
    private int security_type;

    public WifiInformation(String ssid, String bssid, int rssi, int frequency, int link_speed, int tx_link_speed, int max_tx_link_speed, int rx_link_speed, int max_rx_link_speed, int standard, int channel_bandwidth, long timestamp, int security_type) {
        super(timestamp);
        this.ssid = ssid;
        this.bssid = bssid;
        this.rssi = rssi;
        this.frequency = frequency;
        this.link_speed = link_speed;
        this.tx_link_speed = tx_link_speed;
        this.max_tx_link_speed = max_tx_link_speed;
        this.rx_link_speed = rx_link_speed;
        this.max_rx_link_speed = max_rx_link_speed;
        this.standard = standard;
        this.channel_bandwidth = channel_bandwidth;
        this.security_type = security_type;
    }

    public WifiInformation(ScanResult scanResult, long timestamp) {
        super(timestamp);
        ssid = scanResult.SSID;
        bssid = scanResult.BSSID;
        rssi = scanResult.level;
        frequency = scanResult.frequency;
        channel_bandwidth = scanResult.channelWidth;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            scanResult.getSecurityTypes();
        }
    }

    public WifiInformation(WifiInfo wifiInfo, long timestamp) {
        super(timestamp);
        ssid = wifiInfo.getSSID().replace("\"", "");
        bssid = wifiInfo.getBSSID();
        rssi = wifiInfo.getRssi();
        frequency = wifiInfo.getFrequency();
        link_speed = wifiInfo.getLinkSpeed();
        max_tx_link_speed = wifiInfo.getMaxSupportedTxLinkSpeedMbps();
        tx_link_speed = wifiInfo.getTxLinkSpeedMbps();
        max_rx_link_speed = wifiInfo.getMaxSupportedRxLinkSpeedMbps();
        rx_link_speed = wifiInfo.getTxLinkSpeedMbps();
        standard = wifiInfo.getWifiStandard();
        security_type = wifiInfo.getCurrentSecurityType();
    }

    public int getSecurity_type() {
        return security_type;
    }

    public void setSecurity_type(int security_type) {
        this.security_type = security_type;
    }

    public String getWifiSecurityTypeString(int type) {
        switch (type) {
            case WifiInfo.SECURITY_TYPE_UNKNOWN:
                return "Unknown";
            case WifiInfo.SECURITY_TYPE_OPEN:
                return "Open";
            case WifiInfo.SECURITY_TYPE_WEP:
                return "WEP";
            case WifiInfo.SECURITY_TYPE_PSK:
                return "PSK";
            case WifiInfo.SECURITY_TYPE_EAP:
                return "EAP";
            case WifiInfo.SECURITY_TYPE_SAE:
                return "SAE";
            case WifiInfo.SECURITY_TYPE_EAP_WPA3_ENTERPRISE_192_BIT:
                return "WPA3 EP 192Bit";
            case WifiInfo.SECURITY_TYPE_OWE:
                return "OWE";
            case WifiInfo.SECURITY_TYPE_WAPI_PSK:
                return "WAPI PSK";
            case WifiInfo.SECURITY_TYPE_WAPI_CERT:
                return "WAPI Cert";
            case WifiInfo.SECURITY_TYPE_EAP_WPA3_ENTERPRISE:
                return "EAP WPA3 EP";
            case WifiInfo.SECURITY_TYPE_OSEN:
                return "OSEN";
            case WifiInfo.SECURITY_TYPE_PASSPOINT_R1_R2:
                return "Passpoint R1 R2";
            case WifiInfo.SECURITY_TYPE_PASSPOINT_R3:
                return "Passpoint R3";
            default:
                return "Unknown";
        }
    }

    public String getSecurityTypeString() {
        return getWifiSecurityTypeString(security_type);
    }

    public String getWifiStandardString(int standard) {
        switch (standard) {
            case WIFI_STANDARD_UNKNOWN:
                return "Unknown";
            case WIFI_STANDARD_LEGACY:
                return "Legacy";
            case WIFI_STANDARD_11N:
                return "11n";
            case WIFI_STANDARD_11AC:
                return "11ac";
            case WIFI_STANDARD_11AX:
                return "11ax";
            case WIFI_STANDARD_11AD:
                return "11ad";
            case WIFI_STANDARD_11BE:
                return "11be";
            default:
                return "Unknown";
        }
    }

    public String getStandardString() {
        return getWifiStandardString(standard);
    }

    public String getChannelBandwidthStringFromInt(int channel_bandwidth) {
        switch (channel_bandwidth) {
            case CHANNEL_WIDTH_20MHZ:
                return "20 MHz";
            case CHANNEL_WIDTH_40MHZ:
                return "40 MHz";
            case CHANNEL_WIDTH_80MHZ:
                return "80 MHz";
            case CHANNEL_WIDTH_160MHZ:
                return "160 MHz";
            case CHANNEL_WIDTH_80MHZ_PLUS_MHZ:
                return "80 MHz + 80 MHz";
            case CHANNEL_WIDTH_320MHZ:
                return "320 MHz";
            default:
                return "N/A";
        }
    }

    public String getChannelBandwithString() {
        return getChannelBandwidthStringFromInt(channel_bandwidth);
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getLink_speed() {
        return link_speed;
    }

    public void setLink_speed(int link_speed) {
        this.link_speed = link_speed;
    }

    public int getTx_link_speed() {
        return tx_link_speed;
    }

    public void setTx_link_speed(int tx_link_speed) {
        this.tx_link_speed = tx_link_speed;
    }

    public int getMax_tx_link_speed() {
        return max_tx_link_speed;
    }

    public void setMax_tx_link_speed(int max_tx_link_speed) {
        this.max_tx_link_speed = max_tx_link_speed;
    }

    public int getRx_link_speed() {
        return rx_link_speed;
    }

    public void setRx_link_speed(int rx_link_speed) {
        this.rx_link_speed = rx_link_speed;
    }

    public int getMax_rx_link_speed() {
        return max_rx_link_speed;
    }

    public void setMax_rx_link_speed(int max_rx_link_speed) {
        this.max_rx_link_speed = max_rx_link_speed;
    }

    public int getStandard() {
        return standard;
    }

    public void setStandard(int standard) {
        this.standard = standard;
    }

    public int getChannel_bandwidth() {
        return channel_bandwidth;
    }

    public void setChannel_bandwidth(int channel_bandwidth) {
        this.channel_bandwidth = channel_bandwidth;
    }

    public void setChannel_width(int channel_bandwidth) {
        this.channel_bandwidth = channel_bandwidth;
    }

    /**
     * Return a influx point representation of the wifi information
     *
     * @return Influx Point
     */
    public com.influxdb.client.write.Point getWifiInformationPoint() {
        Point point = new Point("WifiInformation");
        point.time(System.currentTimeMillis(), WritePrecision.MS);
        point.addField("SSID", getSsid());
        point.addField("BSSID", getBssid());
        point.addField("RSSI", getRssi());
        point.addField("Frequency", getFrequency());
        point.addField("Link Speed", getLink_speed());
        point.addField("TX Link Speed", getTx_link_speed());
        point.addField("Max Supported TX Speed", getMax_tx_link_speed());
        point.addField("RX Link Speed", getRx_link_speed());
        point.addField("Max Supported RX Speed", getMax_rx_link_speed());
        point.addField("Standard", getStandardString());
        point.addField("Channel Width", getChannelBandwithString());
        point.addField("Security Type", getSecurityTypeString());
        return point;
    }

}

