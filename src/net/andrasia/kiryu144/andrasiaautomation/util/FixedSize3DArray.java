package net.andrasia.kiryu144.andrasiaautomation.util;

import net.andrasia.kiryu144.andrasiaautomation.structure.block.SingleStructureBlock;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.util.Vector;

import java.util.*;

public class FixedSize3DArray<T> implements ConfigurationSerializable, Iterable<T> {
    protected ArrayList<T> data;
    protected Vector dimensions;

    public FixedSize3DArray(Vector dimensions){
        this.dimensions = dimensions;
        initializeArray();
    }

    public FixedSize3DArray(Map<String, Object> serialization) {
        this.dimensions = (Vector) serialization.get("size");
        data = new ArrayList<>(dimensions.getBlockX() * dimensions.getBlockY() * dimensions.getBlockZ());
        data.addAll((List<T>) serialization.get("data"));
    }

    private void initializeArray(){
        int amount = dimensions.getBlockX() * dimensions.getBlockY() * dimensions.getBlockZ();
        data = new ArrayList<>(amount);
        for(int i = 0; i < amount; ++i){
            data.add(null);
        }
    }

    protected void validatePosition(Vector position){
        Validate.isTrue(position.getBlockX() >= 0 && position.getBlockX() < dimensions.getBlockX(), String.format("Coordinate X out of boundaries (%d)", position.getBlockX()));
        Validate.isTrue(position.getBlockY() >= 0 && position.getBlockY() < dimensions.getBlockY(), String.format("Coordinate Y out of boundaries (%d)", position.getBlockX()));
        Validate.isTrue(position.getBlockZ() >= 0 && position.getBlockZ() < dimensions.getBlockZ(), String.format("Coordinate Z out of boundaries (%d)", position.getBlockX()));
    }

    protected int getIndexForPosition(Vector position){
        validatePosition(position);
        return position.getBlockX() * getDimensions().getBlockY() * getDimensions().getBlockZ() + position.getBlockY() * getDimensions().getBlockZ() + position.getBlockZ();
    }

    public void set(Vector position, T value){
        data.set(getIndexForPosition(position), value);
    }

    public T get(Vector position){
        return data.get(getIndexForPosition(position));
    }

    public T get(int index){
        return data.get(index);
    }

    public Vector getDimensions() {
        return dimensions;
    }

    public int size() {
        return data.size();
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> serialized = new HashMap<>();
        serialized.put("size", dimensions);
        serialized.put("data", data);
        return serialized;
    }

    public class Iterator implements java.util.Iterator<T> {
        private int index = 0;

        public boolean hasNext() {
            return index < size();
        }

        public T next() {
            return get(index++);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Vector toVector() {
            int i = index - 1;
            int x = i / (getDimensions().getBlockY() * getDimensions().getBlockZ());
            int y = (i - x * getDimensions().getBlockY() * getDimensions().getBlockZ()) / getDimensions().getBlockZ();
            int z = i - x * getDimensions().getBlockY() * getDimensions().getBlockZ() - y * getDimensions().getBlockZ();
            return new Vector(x, y, z);
        }
    }

    @Override
    public Iterator iterator() {
        return new Iterator();
    }
}
