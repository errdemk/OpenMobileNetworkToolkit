/*
 *  SPDX-FileCopyrightText: 2023 Peter Hasse <peter.hasse@fokus.fraunhofer.de>
 *  SPDX-FileCopyrightText: 2023 Johann Hackler <johann.hackler@fokus.fraunhofer.de>
 *  SPDX-FileCopyrightText: 2023 Fraunhofer FOKUS
 *
 *  SPDX-License-Identifier: BSD-3-Clause-Clear
 */

package de.fraunhofer.fokus.OpenMobileNetworkToolkit.DataProvider;

import android.content.Context;
import android.widget.TableLayout;

public class BatteryInformation extends Information {
    private int scale;
    private int level;
    private int charge_type;
    public BatteryInformation() {
        super();
    }
    public BatteryInformation(long timeStamp, int scale, int level, int charge_type) {
        super(timeStamp);
        this.scale = scale;
        this.level = level;
        this.charge_type = charge_type;
    }
    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCharge_type() {
        return charge_type;
    }

    public void setCharge_type(int charge_type) {
        this.charge_type = charge_type;
    }

    public double getPercent() {
        return level * 100 / (double) scale;
    }


}
