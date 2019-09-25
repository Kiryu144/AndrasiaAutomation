package net.andrasia.kiryu144.andrasiaautomation.structure;

import net.andrasia.kiryu144.andrasiaautomation.structure.block.StructureBlock;
import net.andrasia.kiryu144.andrasiaautomation.structure.controller.StructureController;
import net.andrasia.kiryu144.andrasiaautomation.util.FixedSize3DArray;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Structure implements ConfigurationSerializable {
    protected FixedSize3DArray<StructureBlock> blocks;
    protected Vector centerOffset;
    protected String id;
    protected String name;

    protected Class<? extends StructureController> structureInstanceClass;
    protected HashMap<String, Object> structureInstanceData;

    public Structure(String id, Vector size, Vector centerOffset, String name){
        this.id = id;
        this.centerOffset = (centerOffset != null) ? centerOffset : new Vector(0, 0, 0);
        this.name = (name != null) ? name : "unnamed";
        blocks = new FixedSize3DArray(size);
    }

    public Structure(Map<String, Object> serialized){
        this.id = (String) serialized.get("id");
        this.centerOffset = Vector.deserialize((Map<String, Object>) serialized.get("center_offset"));
        this.name = (String) serialized.get("name");
        this.blocks = new FixedSize3DArray<>((HashMap<String, Object>) serialized.get("blocks"));
        this.structureInstanceClass = (Class<? extends StructureController>) serialized.get("structure_instance_class");
        this.structureInstanceData = (HashMap<String, Object>) serialized.get("structure_instance_class_data");
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> serialization = new HashMap<>();
        serialization.put("blocks", blocks.serialize());
        serialization.put("center_offset", centerOffset.serialize());
        serialization.put("id", id);
        serialization.put("name", name);
        serialization.put("structure_instance_class", structureInstanceClass);
        serialization.put("structure_instance_class_data", structureInstanceData);
        return serialization;
    }

    public FixedSize3DArray<StructureBlock> getBlocks() {
        return blocks;
    }

    public Vector findFirstNonMatchingBlock(Location location){
        for(FixedSize3DArray<StructureBlock>.Iterator it = blocks.iterator(); it.hasNext(); ) {
            StructureBlock structureBlock = it.next();
            Vector   localPosition = it.toVector();
            Location worldLocation = location.clone().add(localPosition);

            if(structureBlock != null && !structureBlock.matches(worldLocation.getBlock().getType())) {
                return localPosition;
            }
        }
        return null;
    }

    public boolean matches(Location location){
        return findFirstNonMatchingBlock(location) == null;
    }

    public Vector getCenterOffset() {
        return centerOffset;
    }

    public void setCenterOffset(Vector centerOffset) {
        this.centerOffset = centerOffset;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Class<? extends StructureController> getStructureInstanceClass() {
        return structureInstanceClass;
    }

    public void setStructureInstanceClass(Class<? extends StructureController> structureInstanceClass) {
        this.structureInstanceClass = structureInstanceClass;
    }

    public HashMap<String, Object> getStructureInstanceData() {
        return structureInstanceData;
    }

    public void setStructureInstanceData(HashMap<String, Object> structureInstanceData) {
        this.structureInstanceData = structureInstanceData;
    }
}













