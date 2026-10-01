// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.core;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.stream.IntStream;
import static org.junit.jupiter.api.Assertions.*;
class DisplayPagesTest {
 @Test void everyLineRemainsReachableAcrossPages(){var input=IntStream.range(0,70).mapToObj(i->"line"+i).toList();var output=new ArrayList<String>();for(int page=0;page<DisplayPages.count(input.size(),8);page++)output.addAll(DisplayPages.slice(input,page,8));assertEquals(input,output);}
 @Test void shortenedInputClampsStalePageAndEmptyIsSafe(){assertEquals(List.of("a","b"),DisplayPages.slice(List.of("a","b"),999,4));assertEquals(List.of(),DisplayPages.slice(List.of(),-20,8));assertEquals(1,DisplayPages.count(0,8));}
 @Test void autoCycleUsesServerTickAndManualModeStaysPut(){assertEquals(1,DisplayPages.next(0,3,40,40));assertEquals(0,DisplayPages.next(2,3,80,40));assertEquals(2,DisplayPages.next(2,3,81,40));assertEquals(2,DisplayPages.next(2,3,80,0));}
 @Test void limitsRejectImpossiblePageSizes(){assertEquals(18,DisplayPages.count(70,-1));assertEquals(3,DisplayPages.count(70,999));}
}
