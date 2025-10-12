package org.gi.gICore.model.item;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ListData implements PersistentDataType<String, List<String>> {
    //  실제 NBT(PDC)에 저장되는 데이터의 타입
    //  내부적으로는 String으로 저장된다.
    @NotNull
    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    // 플러그인 코드에서 다루는 타입
    // 우리는 List<String> 형태로 데이터를 get/set 한다.
    @NotNull
    @Override
    public Class<List<String>> getComplexType() {
        return (Class) List.class;
    }
    // List<String> → String 변환 (저장 시)
    // 리스트를 세미콜론(;)으로 연결한 문자열로 직렬화한다.
    @NotNull
    @Override
    public String toPrimitive(@NotNull List<String> complex, @NotNull PersistentDataAdapterContext context) {
        return String.join(";", complex);
    }

    // String → List<String> 변환 (불러올 때)
    // 세미콜론(;)으로 구분된 문자열을 리스트로 복원한다.
    @NotNull
    @Override
    public List<String> fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
        return Arrays.asList(primitive.split(";"));
    }
}
