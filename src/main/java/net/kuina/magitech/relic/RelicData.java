package net.kuina.magitech.relic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record RelicData(
    String id,
    RelicRarity rarity,
    RelicShape shape,
    Map<RelicEffectType, Float> mainEffects,
    Map<RelicEffectType, Float> subEffects,
    RelicElement element
) {
    private static final Codec<RelicShape> SHAPE_CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.INT.fieldOf("width").forGetter(RelicShape::width),
            Codec.INT.fieldOf("height").forGetter(RelicShape::height),
            Codec.list(Codec.BYTE).fieldOf("cells").forGetter(s -> {
                List<Byte> list = new ArrayList<>(s.width() * s.height());
                for (int i = 0; i < s.width() * s.height(); i++) {
                    list.add((byte) (s.cells().get(i) ? 1 : 0));
                }
                return list;
            })
        ).apply(instance, (w, h, cells) -> {
            BitSet bits = new BitSet(w * h);
            for (int i = 0; i < cells.size() && i < w * h; i++) {
                if (cells.get(i) != 0) bits.set(i);
            }
            return new RelicShape(w, h, bits);
        })
    );

    private static final Codec<Map<RelicEffectType, Float>> EFFECTS_CODEC =
        Codec.unboundedMap(StringRepresentable.fromEnum(RelicEffectType::values), Codec.FLOAT);

    public static final Codec<RelicData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.fieldOf("id").forGetter(RelicData::id),
            StringRepresentable.fromEnum(RelicRarity::values).fieldOf("rarity").forGetter(RelicData::rarity),
            SHAPE_CODEC.fieldOf("shape").forGetter(RelicData::shape),
            EFFECTS_CODEC.fieldOf("main_effects").forGetter(RelicData::mainEffects),
            EFFECTS_CODEC.fieldOf("sub_effects").forGetter(RelicData::subEffects),
            StringRepresentable.fromEnum(RelicElement::values).fieldOf("element").forGetter(RelicData::element)
        ).apply(instance, RelicData::new)
    );

    public static final StreamCodec<ByteBuf, RelicData> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public RelicData decode(ByteBuf buf) {
            String id = ByteBufCodecs.STRING_UTF8.decode(buf);
            RelicRarity rarity = RelicRarity.values()[buf.readByte() & 0xFF];
            int w = ByteBufCodecs.VAR_INT.decode(buf);
            int h = ByteBufCodecs.VAR_INT.decode(buf);
            byte[] cells = new byte[w * h];
            buf.readBytes(cells);
            BitSet bits = new BitSet(w * h);
            for (int i = 0; i < cells.length; i++) {
                if (cells[i] != 0) bits.set(i);
            }
            RelicShape shape = new RelicShape(w, h, bits);
            Map<RelicEffectType, Float> mainEffects = readEffectMap(buf);
            Map<RelicEffectType, Float> subEffects = readEffectMap(buf);
            RelicElement element = RelicElement.values()[buf.readByte() & 0xFF];
            return new RelicData(id, rarity, shape, mainEffects, subEffects, element);
        }

        @Override
        public void encode(ByteBuf buf, RelicData data) {
            ByteBufCodecs.STRING_UTF8.encode(buf, data.id());
            buf.writeByte(data.rarity().ordinal() & 0xFF);
            ByteBufCodecs.VAR_INT.encode(buf, data.shape().width());
            ByteBufCodecs.VAR_INT.encode(buf, data.shape().height());
            byte[] cells = new byte[data.shape().width() * data.shape().height()];
            for (int i = 0; i < cells.length; i++) {
                cells[i] = (byte) (data.shape().cells().get(i) ? 1 : 0);
            }
            buf.writeBytes(cells);
            writeEffectMap(buf, data.mainEffects());
            writeEffectMap(buf, data.subEffects());
            buf.writeByte(data.element().ordinal() & 0xFF);
        }
    };

    private static Map<RelicEffectType, Float> readEffectMap(ByteBuf buf) {
        int size = ByteBufCodecs.VAR_INT.decode(buf);
        Map<RelicEffectType, Float> map = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            RelicEffectType type = RelicEffectType.values()[buf.readByte() & 0xFF];
            float value = buf.readFloat();
            map.put(type, value);
        }
        return map;
    }

    private static void writeEffectMap(ByteBuf buf, Map<RelicEffectType, Float> map) {
        ByteBufCodecs.VAR_INT.encode(buf, map.size());
        for (var entry : map.entrySet()) {
            buf.writeByte(entry.getKey().ordinal() & 0xFF);
            buf.writeFloat(entry.getValue());
        }
    }

    public String getDisplayName() {
        return "relic.magitech." + id;
    }

    public String getDescriptionKey() {
        return "relic.magitech." + id + ".desc";
    }
}
