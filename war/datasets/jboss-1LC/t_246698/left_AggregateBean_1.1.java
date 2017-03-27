package org.jboss.metadata.aggregate;

import java.lang.reflect.*;
import java.util.*;
import org.jboss.metadata.*;

public class AggregateBean extends AggregateMetaData implements BeanMetaData {
    private AggregateContainer container;
    private AggregateMethod[] methods;
    private AggregateMethod[] homeMethods;
    private AggregateField[] fields;
    private String name;

    public AggregateBean(String name) {
        this.name = name;
        methods = homeMethods = new AggregateMethod[0];
        container = new AggregateContainer();
        fields = new AggregateField[0];
    }

    public AggregateBean(String name, BeanMetaData[] plugins) {
        this(name);
        for(int i=0; i<plugins.length; i++)
            addPlugin(plugins[i]);
    }

    public void addPlugin(BeanMetaData plugin) {
        super.addPlugin(plugin);
        methods = mergeMethods(plugin.getMethods(), methods, plugin.getManager());
        homeMethods = mergeMethods(plugin.getHomeMethods(), homeMethods, plugin.getManager());
        fields = mergeFields(plugin.getFields(), fields, plugin.getManager());
        container.addPlugin(plugin.getContainer());
    }

    private static MethodMetaData getMethod(Set source, String name, Class[] args) {
        Iterator it = source.iterator();
        while(it.hasNext()) {
            MethodMetaData mmd = (MethodMetaData)it.next();
            if(mmd.getName().equals(name) &&
               paramsMatch(mmd.getParameterTypes(), args))
                return mmd;
        }
        throw new IllegalArgumentException();
    }

    private static FieldMetaData getField(Set source, String name) {
        Iterator it = source.iterator();
        while(it.hasNext()) {
            FieldMetaData fmd = (FieldMetaData)it.next();
            if(fmd.getName().equals(name))
                return fmd;
        }
        throw new IllegalArgumentException();
    }

    private static AggregateMethod[] mergeMethods(Set incoming, AggregateMethod[] existing, MetaDataPlugin manager) {
        for(int i=0; i<existing.length; i++) {
            String name = existing[i].getName();
            Class[] args = existing[i].getParameterTypes();
            try {
                MethodMetaData mmd = getMethod(incoming, name, args);
                existing[i].addPlugin(mmd);
                incoming.remove(mmd);
            } catch(IllegalArgumentException e) {
                try {
                    Class cls = manager.getMethodClass();
                    Object instance = cls.newInstance();
                    Method m = cls.getMethod("setName", new Class[]{String.class});
                    m.invoke(instance, new Object[]{name});
                    m = cls.getMethod("setParameterTypes", new Class[]{Class[].class});
                    m.invoke(instance, new Object[]{args});
                    existing[i].addPlugin((MethodMetaData)instance);
                } catch(Exception e2) {e2.printStackTrace();}
            }
        }
        Vector v = new Vector(Arrays.asList(existing));
        for(Iterator it = incoming.iterator(); it.hasNext();) {
            MethodMetaData mmd = (MethodMetaData)it.next();
            String name = mmd.getName();
            Class[] args = mmd.getParameterTypes();
            MethodMetaData[] list = new MethodMetaData[MetaDataFactory.getPluginCount()];
            for(int i=0; i<list.length; i++) {
                Class cls = MetaDataFactory.getPlugin(i).getMethodClass();
                if(cls.equals(mmd.getClass()))
                    list[i] = mmd;
                else
                    try {
                        list[i] = (MethodMetaData)cls.newInstance();
                        Method m = cls.getMethod("setName", new Class[]{String.class});
                        m.invoke(list[i], new Object[]{name});
                        m = cls.getMethod("setParameterTypes", new Class[]{Class[].class});
                        m.invoke(list[i], new Object[]{args});
                    } catch(Exception e) {e.printStackTrace();}
            }
            v.addElement(new AggregateMethod(name, args, list));
        }
        if(v.size() > existing.length)
            return (AggregateMethod[])v.toArray(new AggregateMethod[v.size()]);
        else
            return existing;
    }

    private static AggregateField[] mergeFields(Set incoming, AggregateField[] existing, MetaDataPlugin manager) {
        for(int i=0; i<existing.length; i++) {
            String name = existing[i].getName();
            try {
                FieldMetaData fmd = getField(incoming, name);
                existing[i].addPlugin(fmd);
                incoming.remove(fmd);
            } catch(IllegalArgumentException e) {
                try {
                    Class cls = manager.getFieldClass();
                    Object instance = cls.newInstance();
                    Method m = cls.getMethod("setName", new Class[]{String.class});
                    m.invoke(instance, new Object[]{name});
                    existing[i].addPlugin((FieldMetaData)instance);
                } catch(Exception e2) {e2.printStackTrace();}
            }
        }
        Vector v = new Vector(Arrays.asList(existing));
        for(Iterator it = incoming.iterator(); it.hasNext();) {
            FieldMetaData fmd = (FieldMetaData)it.next();
            String name = fmd.getName();
            FieldMetaData[] list = new FieldMetaData[MetaDataFactory.getPluginCount()];
            for(int i=0; i<list.length; i++) {
                Class cls = MetaDataFactory.getPlugin(i).getFieldClass();
                if(cls.equals(fmd.getClass()))
                    list[i] = fmd;
                else
                    try {
                        list[i] = (FieldMetaData)cls.newInstance();
                        Method m = cls.getMethod("setName", new Class[]{String.class});
                        m.invoke(list[i], new Object[]{name});
                    } catch(Exception e) {e.printStackTrace();}
            }
            v.addElement(new AggregateField(name, list));
        }
        if(v.size() > existing.length)
            return (AggregateField[])v.toArray(new AggregateField[v.size()]);
        else
            return existing;
    }

    public MethodMetaData getMethod(String name, Class[] args) {
        for(int i=0; i<methods.length; i++)
            if(methods[i].getName().equals(name) &&
               paramsMatch(methods[i].getParameterTypes(), args))
                return methods[i];
        throw new IllegalArgumentException("Method not found");
    }

    public MethodMetaData getHomeMethod(String name, Class[] args) {
        for(int i=0; i<homeMethods.length; i++)
            if(homeMethods[i].getName().equals(name) &&
               paramsMatch(homeMethods[i].getParameterTypes(), args))
                return methods[i];
        throw new IllegalArgumentException("Method not found");
    }

    public FieldMetaData getField(String name) {
        for(int i=0; i<fields.length; i++)
            if(fields[i].getName().equals(name))
                return fields[i];
        throw new IllegalArgumentException("Field not found");
    }

    public Set getMethods() {
        return new HashSet(Arrays.asList(methods));
    }

    public Set getHomeMethods() {
        return new HashSet(Arrays.asList(homeMethods));
    }

    public Set getFields() {
        return new HashSet(Arrays.asList(fields));
    }

    public String getName() {
        return name;
    }

    public ContainerMetaData getContainer() {
        return container;
    }

    private static boolean paramsMatch(Class[] one, Class[] two) {
        if(one.length != two.length)
            return false;
        for(int i=0; i<one.length; i++)
            if(!one[i].equals(two[i]))
                return false;
        return true;
    }
}
