package com.example.application.views.support;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.internal.Pair;

public class ViewHelper {
    public static void alert(Element element){
        Style style = element.getStyle();
        style.set("margin","0.1em");
        style.set("padding","0.1em");
        style.set("border-radius","0.5em");
        style.set("border-width","1px");
        style.set("border-style","dashed");
        style.set("border-color","red");
    }

    public static void noAlert(Element element){
        Style style = element.getStyle();
        style.remove("margin");
        style.remove("padding");
        style.remove("border");
    }

    public static Pair<Focusable,Boolean> attention(AbstractField field, String message, Focusable fieldGoto) {

        Notification.show(message, 3000,
                Notification.Position.BOTTOM_START);
        Focusable toField = fieldGoto == null ? (Focusable) field : fieldGoto;
        Pair<Focusable,Boolean> result = new Pair<Focusable,Boolean>(toField,false);
        alert(field.getElement());
        return result;
    }
}
