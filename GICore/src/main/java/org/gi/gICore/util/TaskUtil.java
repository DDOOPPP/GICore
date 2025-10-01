package org.gi.gICore.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * TaskUtil
 * - Bukkit 메인 스레드(게임 로직)와 비동기 스레드(I/O, 계산)를 안전하게 오가도록 도와주는 유틸리티.
 * - 자주 쓰는 제어기(디바운스/스로틀/싱글플라이트)와 안정성 도구(timeout/retry) 포함.
 *
 * 사용 원칙:
 * 1) 느린 작업(DB/HTTP/파일 I/O)은 비동기에서
 * 2) Bukkit API 호출은 메인 스레드에서
 * 3) 비동기 → 메인 반영은 thenSync로
 */
public class TaskUtil {
    /** Bukkit 스케줄러에 넘길 플러그인 참조 */
    private static JavaPlugin plugin;

    /** 비동기 타이머/재시도/타임아웃 등에 쓰는 스케줄러(데몬) */
    private static final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(
                    Math.max(2, Runtime.getRuntime().availableProcessors() / 2),
                    r -> {
                        Thread t = new Thread(r, "TaskUtil-Async");
                        t.setDaemon(true); // 서버 종료 시 자동 종료
                        return t;
                    });

    /** 디바운스: 같은 key 호출 중 마지막 한 번만 지정 지연 후 실행 */
    private static final ConcurrentHashMap<String, BukkitTask> debounceMap = new ConcurrentHashMap<>();
    /** 스로틀: 같은 key에 대해 주기 내 1회만 통과 */
    private static final ConcurrentHashMap<String, Long> throttleMap = new ConcurrentHashMap<>();
    /** 싱글플라이트: 같은 key의 중복 비동기 호출을 1회로 병합 */
    private static final ConcurrentHashMap<String, CompletableFuture<?>> singleFlightMap = new ConcurrentHashMap<>();

    /**
     * 초기화: Bukkit 스케줄러에서 사용할 플러그인을 등록.
     * - 반드시 onEnable 등에서 한 번 호출.
     * - 플러그인 null이면 NPE 발생(의도).
     * - 필요하면 isEnabled 검사/비활성화 로직을 추가할 수 있음.
     */
    public static void init(JavaPlugin pl) {
        // FIX: 기존 코드 버그 -> plugin 필드가 아니라 인자로 받은 pl를 검사해야 함.
        Objects.requireNonNull(pl, "plugin cannot be null");
        // Optional: 상태 검증을 원한다면 아래 주석 해제
        // if (!pl.isEnabled()) {
        //     Bukkit.getPluginManager().disablePlugin(pl);
        //     throw new IllegalStateException("Plugin must be enabled before TaskUtil.init");
        // }
        plugin = pl;
    }

    /** 현재 스레드가 Bukkit 메인 스레드인지 여부 */
    public static boolean isMain() {
        return Bukkit.isPrimaryThread();
    }

    /** 메인 스레드에서 runnable 즉시 실행(스케줄링) */
    public static BukkitTask runSync(Runnable runnable) {
        return Bukkit.getScheduler().runTask(plugin, safe(runnable));
    }

    /** 비동기 스레드에서 runnable 즉시 실행(스케줄링) */
    public static BukkitTask runAsync(Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, safe(runnable));
    }

    /** 메인 스레드에서 delay(틱) 후 실행 */
    public static BukkitTask runSyncLater(long delay, Runnable runnable) {
        return Bukkit.getScheduler().runTaskLater(plugin, safe(runnable), delay);
    }

    /** 비동기 스레드에서 delay(틱) 후 실행 */
    public static BukkitTask runAsyncLater(long delay, Runnable runnable) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, safe(runnable), delay);
    }

    /** (호환용, 인자 순서 통일 권장) */
    @Deprecated
    public static BukkitTask runAsyncLater(Runnable runnable, long delay) {
        return runAsyncLater(delay, runnable);
    }

    /** 메인 스레드에서 delay 후 period(틱)마다 반복 실행 */
    public static BukkitTask runSyncTimer(Runnable runnable, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimer(plugin, safe(runnable), delay, period);
    }

    /** 비동기 스레드에서 delay 후 period(틱)마다 반복 실행 */
    public static BukkitTask runAsyncTimer(Runnable runnable, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, safe(runnable), delay, period);
    }

    // -------------------- 메인 <-> 비동기 전환 --------------------

    /**
     * 비동기 스레드에서 Supplier 실행 후 결과를 반환하는 CompletableFuture 생성.
     * - DB/HTTP 같은 I/O에 사용.
     */
    public static <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(wrapSupplier(supplier), scheduler);
    }

    /**
     * 비동기 결과를 받은 뒤, Consumer를 메인 스레드에서 실행.
     * - 반환된 CompletableFuture는 "메인 스레드 consumer 실행까지 끝나야" 완료됨.
     * - Consumer에서 Bukkit API 호출 가능.
     */
    public static <T> CompletableFuture<Void> thenSync(CompletableFuture<T> future, Consumer<T> consumer) {
        CompletableFuture<Void> done = new CompletableFuture<>();
        future.whenComplete((r, e) -> {
            if (e != null) { done.completeExceptionally(e); return; }
            runSync(() -> {
                try { consumer.accept(r); done.complete(null); }
                catch (Throwable t) { log(t); done.completeExceptionally(t); }
            });
        });
        return done;
    }

    /**
     * (비동기 문맥에서) 메인 스레드 전용 연산을 실행하고 결과를 돌려받음.
     * - 예: ItemStack/NBT 등 메인에서만 안전한 로직을 호출해야 할 때.
     */
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

    /** 현재 스레드가 메인이라면 즉시 실행, 아니면 메인 스레드로 넘겨 실행 */
    public static void ensureMainThread(Runnable runnable) {
        if (isMain()) safe(runnable).run();
        else runSync(runnable);
    }

    /** 실행 중 예외를 잡아 로그로 남기고 다시 던져 스케줄러가 감지하게 함 */
    private static Runnable safe(Runnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Throwable t) {
                log(t);
                throw t;
            }
        };
    }

    // -------------------- 타임아웃 / 재시도 --------------------

    /**
     * 특정 작업이 d 동안 끝나지 않으면 TimeoutException으로 실패 처리.
     * - NOTE: 원래 future는 계속 돌 수 있음(취소하지 않음). 필요 시 cancel(true) 추가 고려.
     */
    public static <T> CompletableFuture<T> withTimeout(CompletableFuture<T> future, Duration d) {
        CompletableFuture<T> timeout = new CompletableFuture<>();
        ScheduledFuture<?> sf = scheduler.schedule(
                () -> timeout.completeExceptionally(new TimeoutException("Timeout " + d)),
                d.toMillis(), TimeUnit.MILLISECONDS
        );
        future.whenComplete((r, e) -> sf.cancel(false)); // 정상 종료 시 타이머 해제
        return future.applyToEither(timeout, Function.identity());
    }

    /**
     * 실패 시 재시도. 시도 간격 = 지수 백오프 + 지터(0~250ms).
     * - maxAttempts: 최대 시도 횟수(성공/실패 포함)
     * - baseBackoff: 1회차 기준 지연 시간(예: 200ms) → 2^n * base + jitter
     */
    public static <T> CompletableFuture<T> retryAsync(Supplier<T> supplier, int maxAttempts, Duration baseBackoff) {
        CompletableFuture<T> future = new CompletableFuture<>();
        runAttempt(future, supplier, 1, maxAttempts, baseBackoff);
        return future;
    }

    /** 재귀적으로 재시도 스케줄링 */
    private static <T> void runAttempt(CompletableFuture<T> cf, Supplier<T> supplier, int attempt, int max, Duration baseBackoff) {
        CompletableFuture.supplyAsync(wrapSupplier(supplier), scheduler)
                .whenComplete((val, err) -> {
                    if (err == null) {
                        cf.complete(val);
                    } else if (attempt >= max) {
                        cf.completeExceptionally(err);
                    } else {
                        long delayMs = backoffMillis(attempt, baseBackoff, 250L);
                        scheduler.schedule(
                                () -> runAttempt(cf, supplier, attempt + 1, max, baseBackoff),
                                delayMs, TimeUnit.MILLISECONDS
                        );
                    }
                });
    }

    // -------------------- 디바운스 / 스로틀 / 싱글플라이트 --------------------

    /**
     * 디바운스: 동일 key로 들어온 연속 호출 중 "마지막 호출"만 delayTicks 후 실행.
     * - 예: 검색창 타이핑 후 마지막 입력 기준으로 쿼리 실행.
     */
    public static void debounce(String key, long delayTicks, Runnable r) {
        // compute로 원자적 교체 → 레이스 줄임
        debounceMap.compute(key, (k, prev) -> {
            if (prev != null) prev.cancel();
            return runSyncLater(delayTicks, () -> {
                debounceMap.remove(k);
                safe(r).run();
            });
        });
    }

    /**
     * 스로틀(ms): 동일 key에 대해 periodMs 동안 1회만 통과(true 반환).
     * - 예: 좌클릭/명령 연타 방지.
     */
    public static boolean throttleMs(String key, long periodMs) {
        long now = System.currentTimeMillis();
        Long last = throttleMap.get(key);
        if (last == null || now - last >= periodMs) {
            throttleMap.put(key, now);
            return true;
        }
        return false;
    }

    /** 현재 시간을 틱으로 근사(50ms ≈ 1틱) */
    private static long currentTicks() {
        return System.currentTimeMillis() / 50L;
    }

    /** 지수 백오프(2^(attempt-1)) + 지터, 상한 30s */
    private static long backoffMillis(int attempt, Duration base, long jitterMaxMs) {
        long exp = (long) Math.pow(2, attempt - 1);
        long jitter = ThreadLocalRandom.current().nextLong(jitterMaxMs + 1);
        return Math.min(base.toMillis() * exp + jitter, 30_000);
    }

    /**
     * 싱글플라이트: 동일 key의 중복 비동기 호출을 1회로 병합하고 결과 공유.
     * - computeIfAbsent 내부에서 원자적으로 Future를 등록 → 경쟁 최소화.
     * - 완료/실패/취소 시 맵에서 제거.
     */
    @SuppressWarnings("unchecked")
    public static <T> CompletableFuture<T> singleFlight(String key, Supplier<T> asyncSupplier) {
        CompletableFuture<T> cf = (CompletableFuture<T>) singleFlightMap.computeIfAbsent(key, k ->
                CompletableFuture.supplyAsync(wrapSupplier(asyncSupplier), scheduler)
                        .whenComplete((r, e) -> singleFlightMap.remove(k))
        );
        return cf;
    }

    // -------------------- 플레이어 헬퍼 --------------------

    /**
     * 플레이어가 여전히 온라인일 때만 메인 스레드에서 consumer 실행.
     * - 네트워크 지연 동안 로그아웃한 경우를 안전 처리.
     */
    public static void withOnlinePlayer(UUID uuid, Consumer<Player> consumer) {
        runSync(() -> {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) {
                consumer.accept(p);
            }
        });
    }

    // -------------------- 내부 유틸/로그/정리 --------------------

    /** Supplier 실행 시 예외를 잡아 로그 남기고 재던짐 */
    private static <T> Supplier<T> wrapSupplier(Supplier<T> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (Throwable t) {
                log(t);
                throw t;
            }
        };
    }

    /** 에러 로깅: plugin 로거 + 스택트레이스 출력 */
    private static void log(Throwable t) {
        if (plugin != null) plugin.getLogger().severe("TaskUtil error: " + t.getMessage());
        t.printStackTrace();
    }

    /**
     * 종료: 스케줄러 강제 종료 + 디바운스/스로틀/싱글플라이트 맵 정리.
     * - onDisable 등에서 호출.
     * - awaitTermination으로 짧게 대기하여 리소스 정리.
     */
    public static void shutdown() {
        scheduler.shutdownNow();
        debounceMap.values().forEach(BukkitTask::cancel);
        debounceMap.clear();
        throttleMap.clear();
        singleFlightMap.clear();
        try { scheduler.awaitTermination(1, TimeUnit.SECONDS); } catch (InterruptedException ignored) {}
    }
}
