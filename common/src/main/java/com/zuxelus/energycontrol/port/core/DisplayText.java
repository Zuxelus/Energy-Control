// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.core;
import java.util.*;
/** Original text-card ten-line/formatting contract, bounded for imported data. */
public final class DisplayText {
    private DisplayText() {}
    public static List<String> lines(String text) {
        var result=new ArrayList<String>();
        for(String line:text.replace("\r", "").split("\n",-1)) {
            if(result.size()==10) break;
            line=line.substring(0,Math.min(line.length(),128));
            result.add(line.replace('@','\u00a7').replace("\u00a7\u00a7","@"));
        }
        while(!result.isEmpty() && result.getLast().isEmpty()) result.removeLast();
        return List.copyOf(result);
    }
}
