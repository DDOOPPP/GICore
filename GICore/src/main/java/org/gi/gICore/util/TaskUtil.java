package org.gi.gICore.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.units.qual.C;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class TaskUtil {
    private static TaskUtil instance;
    private static JavaPlugin plugin;
    private static final ScheduledExecutorService schedule = //일종의 비동기 타이머라고 생각하면 됨
            Executors.newScheduledThreadPool(Math.max(2, Runtime.getRuntime().availableProcessors() / 2), //스레드 풀 크기 지정
                    r -> {
                        Thread t = new Thread(r, "TaskUtil-Async");//Runnable을 담아서 스레드를 생성
                        t.setDaemon(true); //데몬 스레드로 지정 -> JVM 종료시 자동 정리 => 해당 스레드로 프로그램 정지되는 것을 방지
                        return t;// 스레드 풀에 등록
                    });
    /** 어떤 이벤트가 연속으로 들어온뒤 일정시간동안 이벤트가 없는경우 호출 (디바운드) */
    private static final ConcurrentHashMap<String, BukkitTask> debounceMap = new ConcurrentHashMap<>();
    /** 주기마다 한번 만 실행 (쓰로틀) */
    private static final ConcurrentHashMap<String, Long> throttleMap = new ConcurrentHashMap<>();
    /** 동일한 요청은 합쳐서 한번만 실행 (싱글 플라이트) */
    private static final ConcurrentHashMap<String, CompletableFuture<?>> singleFlightMap = new ConcurrentHashMap<>();

    public static void init(JavaPlugin pl) {
        plugin = Objects.requireNonNull(pl, "plugin cannot be null");
        if (plugin == null) {
            Bukkit.getPluginManager().disablePlugin(pl);
            return;
        }
    }

    public static boolean isMain(){
        return Bukkit.isPrimaryThread();
    }

    public static TaskUtil getInstance() {
        return instance;
    }
}
