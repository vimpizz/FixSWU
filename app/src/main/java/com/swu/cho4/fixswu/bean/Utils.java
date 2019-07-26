package com.swu.cho4.fixswu.bean;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static List<BoardBean> getSortForDate(List<BoardBean> boardList) {
        return boardList.stream()
                .sorted(Comparator.comparing(BoardBean::getMillisecond))
                .collect(Collectors.toList());
    }
}
