package com.cvl.api.JSON.Model;

public class ModelIO {
    public String type;
    public String name;
    public String description  ;
    public int[] size;
    public ModelIO()
    {}
    public ModelIO(String type, String name, String description)
    {
        this.type = type;
        this.name = name;
        this.description = description;
        this.size = null;
    }
    public ModelIO(String type, String name, String description, int[] size)
    {
        this.type = type;
        this.name = name;
        this.description = description;
        this.size = size;
    }
}
