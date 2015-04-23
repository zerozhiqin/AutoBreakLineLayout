package dev.misono.autobreaklinelayout.simpleinject;

import android.app.Activity;
import android.view.View;

import java.lang.reflect.Field;

public class VInjects {

    public static void injectIntoActivity(Activity act){

        Field[] fields = act.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            VInject inject = field.getAnnotation(VInject.class);
            if(inject != null) {
                int id = inject.value();
                if(id == -1) {
                    try {
                        String packageName = act.getPackageName();
                        Field idField = Class.forName(packageName+".R$id").getField(field.getName());
                        id = idField.getInt(null);
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                field.setAccessible(true);
                try {
                    field.set(act, act.findViewById(id));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void injectIntoView(Object obj, View itemView){
        Field[] fields = obj.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            VInject inject = field.getAnnotation(VInject.class);
            if(inject != null) {
                int id = inject.value();
                if(id == -1) {
                    try {
                        String packageName = itemView.getContext().getPackageName();
                        Field idField = Class.forName(packageName+".R$id").getField(field.getName());
                        id = idField.getInt(null);
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                field.setAccessible(true);
                try {
                    field.set(obj, itemView.findViewById(id));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
