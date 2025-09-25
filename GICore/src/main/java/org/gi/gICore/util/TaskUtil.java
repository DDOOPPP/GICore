package org.gi.gICore.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.units.qual.C;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TaskUtil {
    private static TaskUtil instance;
    private static JavaPlugin plugin;
    private static final ScheduledExecutorService scheduler = //일종의 비동기 타이머라고 생각하면 됨
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

    public static BukkitTask runSync(Runnable runnable) {
        return Bukkit.getScheduler().runTask(plugin, safe(runnable));
    }

    public static BukkitTask runAsync(Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, safe(runnable));
    }

    public static BukkitTask runSyncLater(long delay,Runnable runnable ) {
        return Bukkit.getScheduler().runTaskLater(plugin, safe(runnable), delay);
    }

    public static BukkitTask runAsyncLater(Runnable runnable, long delay) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, safe(runnable), delay);
    }

    public static BukkitTask runSyncTimer(Runnable runnable, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimer(plugin, safe(runnable), delay, period);
    }

    public static BukkitTask runAsyncTimer(Runnable runnable, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, safe(runnable), delay, period);
    }
    /** 메인 <-> 비동기 전환 */

    /** 비동기 실행 결과 반환*/
    public static <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(wrapSupplier(supplier), scheduler);
    }
    /** 비동기 결과를 메인 스레드에서 처리*/
    public static <T> CompletableFuture<Void> thenSync(CompletableFuture<T> future, Consumer<T> consumer) {
        return future.thenAccept(result -> runSync(() -> consumer.accept(result)));
    }

    public static <T> CompletableFuture<T> callSync(Callable<T> callable) {
        CompletableFuture<T> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                future.complete(callable.call());
            } catch (Throwable t) {
                future.completeExceptionally(t);
                log(t);
            }
        });
        return future;
    }

    public static void ensureMainThread(Runnable runnable) {
        if (isMain()) safe(runnable).run();
        else runSync(runnable);
    }

    private static Runnable safe(Runnable runnable){
        return () -> {
            try {
                runnable.run();
            } catch (Throwable t) {
               log(t); throw t;
            }
        };
    }

    /** 타임아웃 및 리트라이 */

    public static <T> CompletableFuture<T> withTimeout(CompletableFuture<T> future, Duration duration) {
        CompletableFuture<T> timeoutFuture = new CompletableFuture<>();
        scheduler.schedule(() -> {
            if (!future.isDone()) timeoutFuture.completeExceptionally(new TimeoutException("Timeout " + duration));
        }, duration.toMillis(), TimeUnit.MILLISECONDS);

        return future.applyToEither(timeoutFuture, Function.identity());
    }

    public static <T> CompletableFuture<T> retryAsync(Supplier<T> supplier,int maxAttempts,Duration backoff){
        CompletableFuture<T> future = new CompletableFuture<>();
        runAttempt(future, supplier, 1, maxAttempts, backoff);
        return future;
    }

    private static <T> void runAttempt(CompletableFuture<T> completableFuture, Supplier<T> supplier,int attempt, int max, Duration backoff){
        CompletableFuture
                .supplyAsync(wrapSupplier(supplier))
                .whenComplete((val,err) -> {
                    if (err == null) completableFuture.complete(val);
                    else if(attempt >= max) completableFuture.completeExceptionally(err);
                    else scheduler.schedule(() -> runAttempt(completableFuture, supplier, attempt + 1, max, backoff), backoff.toMillis(), TimeUnit.MILLISECONDS);
                 });
    }

    /** 동일 key 호출이 연달아 올 때 마지막 호출만 delayTicks 후 실행 */
    public static void debounce(String key, long delayTicks, Runnable r) {
        BukkitTask prev = debounceMap.get(key);
        if (prev != null) prev.cancel();

        BukkitTask task = runSyncLater(delayTicks, () -> {
            debounceMap.remove(key);
            safe(r).run();
        });
        debounceMap.put(key, task);
    }


    /** 동일 key에 대해 periodTicks 내 1회만 통과 (true면 실행 허용) */
    public static boolean throttle(String key, long periodTicks) {
        long now = currentTicks();
        Long last = throttleMap.get(key);
        if (last == null || now - last >= periodTicks) {
            throttleMap.put(key, now);
            return true;
        }
        return false;
    }

    private static long currentTicks() {
        // 대충 50ms = 1 tick 가정 (정확한 서버 틱이 필요하면 별도 틱 카운터를 타이머로 갱신)
        return System.currentTimeMillis() / 50L;
    }

    /* -------------------------- 싱글플라이트(동일 키 중복 병합) -------------------------- */

    @SuppressWarnings("unchecked")
    public static <T> CompletableFuture<T> singleFlight(String key, Supplier<T> asyncSupplier) {
        CompletableFuture<T> existing = (CompletableFuture<T>) singleFlightMap.get(key);
        if (existing != null) return existing;

        CompletableFuture<T> created = supplyAsync(asyncSupplier)
                .whenComplete((r, e) -> singleFlightMap.remove(key));
        CompletableFuture<?> race = singleFlightMap.putIfAbsent(key, created);
        if (race != null) {
            // 누군가 먼저 넣었다면 그걸 사용
            return (CompletableFuture<T>) race;
        }
        return created;
    }

    /** 플레이어가 여전히 온라인일 때만 메인에서 Consumer 실행 */
    public static void withOnlinePlayer(UUID uuid, Consumer<Player> consumer) {
        runSync(() -> {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) {
                consumer.accept(p);
            }
        });
    }

    private static <T> Supplier<T> wrapSupplier(Supplier<T> supplier){
        return () -> {
            try {
                return supplier.get();
            } catch (Throwable t) {
                log(t); throw t;
            }
        };
    }

    private static void log(Throwable t) {
        if (plugin != null) plugin.getLogger().severe("TaskUtil error: " + t.getMessage());
        t.printStackTrace();
    }

    public static void shutdown(){
        scheduler.shutdownNow();
        debounceMap.values().forEach(BukkitTask::cancel);
        debounceMap.clear();
        throttleMap.clear();
        singleFlightMap.clear();
    }

}
