package de.fraunhofer.fokus.OpenMobileNetworkToolkit.DataProvider.CellInformations;

import android.content.Context;
import android.telephony.CellIdentityCdma;
import android.telephony.CellInfoCdma;
import android.telephony.CellSignalStrengthCdma;
import android.widget.TableLayout;

import de.fraunhofer.fokus.OpenMobileNetworkToolkit.DataProvider.PrettyPrintMap;

public class CDMA extends CellInformation {
    private int cmdaDbm;
    private int cmdaEcio;
    private int evdoDbm;
    private int evdoEcio;
    private int evdoSnr;


    public CDMA() {super();}
    public CDMA(long timestamp, CellSignalStrengthCdma cellSignalStrengthCdma){
        super(timestamp);
        cmdaDbm = cellSignalStrengthCdma.getCdmaDbm();
        cmdaEcio = cellSignalStrengthCdma.getCdmaEcio();
        evdoDbm = cellSignalStrengthCdma.getEvdoDbm();
        evdoEcio = cellSignalStrengthCdma.getEvdoEcio();
        evdoSnr = cellSignalStrengthCdma.getEvdoSnr();
        this.setCellType(CellType.CDMA);

    }
    private CDMA(CellInfoCdma cellInfoCdma,
                 CellIdentityCdma cellIdentityCdma,
                 CellSignalStrengthCdma cellSignalStrengthCdma,
                 long timestamp) {
        super(timestamp,
                CellType.CDMA,
                "N/A",
                -1,
                "N/A",
                -1,
                -1,
                cellSignalStrengthCdma.getLevel(),
                cellIdentityCdma.getOperatorAlphaLong().toString(),
                cellSignalStrengthCdma.getAsuLevel(),
                cellInfoCdma.isRegistered(),
                cellInfoCdma.getCellConnectionStatus());
        cmdaDbm = cellSignalStrengthCdma.getCdmaDbm();
        cmdaEcio = cellSignalStrengthCdma.getCdmaEcio();
        evdoDbm = cellSignalStrengthCdma.getEvdoDbm();
        evdoEcio = cellSignalStrengthCdma.getEvdoEcio();
        evdoSnr = cellSignalStrengthCdma.getEvdoSnr();
    }
    public CDMA(CellInfoCdma cellInfoCdma, long timestamp) {
        this(cellInfoCdma, cellInfoCdma.getCellIdentity(), cellInfoCdma.getCellSignalStrength(), timestamp);
    }
    public void setCmdaDbm(int cmdaDbm) {
        this.cmdaDbm = cmdaDbm;
    }

    public void setCmdaEcio(int cmdaEcio) {
        this.cmdaEcio = cmdaEcio;
    }

    public void setEvdoDbm(int evdoDbm) {
        this.evdoDbm = evdoDbm;
    }

    public void setEvdoEcio(int evdoEcio) {
        this.evdoEcio = evdoEcio;
    }

    public void setEvdoSnr(int evdoSnr) {
        this.evdoSnr = evdoSnr;
    }

    public int getCmdaDbm() {
        return cmdaDbm;
    }

    public int getCmdaEcio() {
        return cmdaEcio;
    }

    public int getEvdoDbm() {
        return evdoDbm;
    }

    public int getEvdoEcio() {
        return evdoEcio;
    }

    public int getEvdoSnr() {
        return evdoSnr;
    }

    @Override
    public TableLayout getTable(TableLayout tl, Context context, boolean displayNull) {
        addRows(tl, context, new String[][]{
                {PrettyPrintMap.cellInformation.alphaLong.toString(), String.valueOf(this.getAlphaLong())},
                {PrettyPrintMap.cellInformation.cellType.toString(), String.valueOf(this.getCellType())},
                {PrettyPrintMap.cellInformation.isRegistered.toString(), String.valueOf(this.isRegistered())},
                {PrettyPrintMap.cellInformation.cellConnectionStatus.toString(), String.valueOf(this.getCellConnectionStatus())},
        }, displayNull);

        addDivider(tl, context);

        addRows(tl, context, new String[][]{
                {PrettyPrintMap.cellInformation.cmdaDbm.toString(), String.valueOf(this.getCmdaDbm())},
                {PrettyPrintMap.cellInformation.cmdaEcio.toString(), String.valueOf(this.getCmdaEcio())},
                {PrettyPrintMap.cellInformation.evdoDbm.toString(), String.valueOf(this.getEvdoDbm())},
                {PrettyPrintMap.cellInformation.evdoEcio.toString(), String.valueOf(this.getEvdoEcio())},
                {PrettyPrintMap.cellInformation.evdoSnr.toString(), String.valueOf(this.getEvdoSnr())},
        }, displayNull);

        return tl;
    }
}
