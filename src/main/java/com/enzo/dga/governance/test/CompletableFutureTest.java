package com.enzo.dga.governance.test;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * CompletableFuture: Java提供的用于异步编译的类，可以实现异步编排、并行执行、统一集结等
 * 体会思想就行，把六个数串行执行，和六个并行执行，串行需要2*6s=12s，并行需要2s
 */
public class CompletableFutureTest {
    public static void main(String[] args) {
        // 将给定的集合中的数字元素进行平方计算，然后求集合中数字元素的和
        List<Integer> nums = Arrays.asList(1, 2, 3, 4, 5, 6);

        long start = System.currentTimeMillis();

//      ArrayList<Integer> tmps = new ArrayList<>();
//        for (Integer num : nums) {
//            tmps.add(num * num);
//            try {
//                TimeUnit.SECONDS.sleep(2);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }

        ArrayList<CompletableFuture<Integer>> futures = new ArrayList<>();

        for (Integer num : nums) {
            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(
                    () -> {
                        try {
                            TimeUnit.SECONDS.sleep(2);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        return num * num;
                    }
            );
            futures.add(future);
        }

        // 并行执行，统一集结
        List<Integer> squareNums = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());

        Integer sum = 0;
        for (Integer squareNum : squareNums) {
            sum += squareNum;
        }

        long end = System.currentTimeMillis();
        System.out.println(end - start);
        System.out.println(sum);


    }
}
