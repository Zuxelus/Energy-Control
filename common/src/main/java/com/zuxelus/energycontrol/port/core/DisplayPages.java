// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.core;
import java.util.List;
/** Pure paging rules; the server selects and synchronizes the visible page. */
public final class DisplayPages {
 private DisplayPages(){}
 public static int size(int requested){return Math.clamp(requested,4,32);}
 public static int count(int lines,int requested){return Math.max(1,(Math.max(0,lines)+size(requested)-1)/size(requested));}
 public static <T> List<T> slice(List<T> lines,int requestedPage,int requestedSize){int size=size(requestedSize);int page=Math.clamp(requestedPage,0,count(lines.size(),size)-1);int start=page*size;return List.copyOf(lines.subList(start,Math.min(lines.size(),start+size)));}
 public static int next(int page,int pages,long tick,int interval){int bounded=Math.clamp(page,0,Math.max(0,pages-1));return interval>0&&tick%interval==0?(bounded+1)%Math.max(1,pages):bounded;}
}
