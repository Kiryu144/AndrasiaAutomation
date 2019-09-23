package net.andrasia.kiryu144.andrasiaautomation.structure;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

public class Structure {
    protected StructureBlock[] data;
    protected Vector size;
    protected Vector centerOffset;
    protected String name;

    public Structure(Vector size, Vector centerOffset, String name){
        this.size = size;
        this.centerOffset = (centerOffset != null) ? centerOffset : new Vector(0, 0, 0);
        this.name = (name != null) ? name : "unnamed";

        data = new StructureBlock[size.getBlockX() * size.getBlockY() * size.getBlockZ()];
    }

    protected void validatePosition(Vector position){
        Validate.isTrue(position.getBlockX() >= 0 && position.getBlockX() < size.getBlockX(), String.format("Coordinate X out of boundaries (%d)", position.getBlockX()));
        Validate.isTrue(position.getBlockY() >= 0 && position.getBlockY() < size.getBlockY(), String.format("Coordinate Y out of boundaries (%d)", position.getBlockX()));
        Validate.isTrue(position.getBlockZ() >= 0 && position.getBlockZ() < size.getBlockZ(), String.format("Coordinate Z out of boundaries (%d)", position.getBlockX()));
    }

    protected int getIndexForPosition(Vector position){
        validatePosition(position);
        return (position.getBlockX() + size.getBlockX() * (position.getBlockY() + size.getBlockY() * position.getBlockZ()));
    }

    public void set(Vector position, StructureBlock block){
        data[getIndexForPosition(position)] = block;
    }

    public StructureBlock get(Vector position){
        return data[getIndexForPosition(position)];
    }

    public Vector findFirstNonMatchingBlock(Location location){
        for(int x = location.getBlockX(); x < location.getBlockX() + size.getBlockX(); ++x){
            for(int y = location.getBlockY(); y < location.getBlockY() + size.getBlockY(); ++y){
                for(int z = location.getBlockZ(); z < location.getBlockZ() + size.getBlockZ(); ++z){
                    Vector position = new Vector(x, y, z);
                    Location worldLocation = new Location(location.getWorld(), x, y, z);
                    Material atLocation = worldLocation.getBlock().getType();
                    Location localLocation = worldLocation.clone().subtract(location);
                    StructureBlock atStructure = get(localLocation.toVector());

                    if(atStructure == null){
                        if(!(atLocation.equals(Material.AIR) || atLocation.equals(Material.CAVE_AIR) || atLocation.equals(Material.VOID_AIR))){
                            return position;
                        }
                    }else{
                        if(!atStructure.matches(atLocation)) {
                            return position;
                        }
                    }
                }
            }
        }

        return null;
    }

    public boolean matches(Location location){
        return findFirstNonMatchingBlock(location) == null;
    }

    public Vector getSize() {
        return size;
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
}













