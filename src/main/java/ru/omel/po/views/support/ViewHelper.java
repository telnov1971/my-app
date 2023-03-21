package ru.omel.po.views.support;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.internal.Pair;
//import org.checkerframework.checker.units.qual.C;
import ru.omel.po.data.entity.User;

import java.util.ArrayList;
import java.util.List;

public class ViewHelper {

    public enum FieldName {DEMANDER, DELEGATE, INN, OGRN, CONTACT, MEEMAIL
        , PASPORTSERIES, PASPORTNUMBER, PASPORTISSUED, ADDRESSREGISTRATION}
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
        style.set("border-width","0px");
    }

    public static Pair<Focusable,Boolean> attention(AbstractField field
            , String message
            , Focusable fieldGoto
            , TextArea space) {

        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setText(message);
        notification.setPosition(Notification.Position.BOTTOM_START);
        notification.setDuration(3000);
        notification.open();

        space.setLabel("Ошибки заполенеия");
        space.setValue(space.getValue() + "\n" + message);
        Focusable toField = fieldGoto == null ? (Focusable) field : fieldGoto;
        Pair<Focusable,Boolean> result = new Pair<>(toField,false);
        alert(field.getElement());
        return result;
    }

    public static void deselect(AbstractField field){
        if(!field.isEmpty()) {
            noAlert(field.getElement());
        }
    }

    public static <C> Select<C> createSelect(ItemLabelGenerator<C> gen, List<C> list,
                                         String label, Class<C> clazz){
        Select<C> select = new Select<>();
        select.setLabel(label);
        select.setItemLabelGenerator(gen);
        select.setItems(list);
        return select;
    }
}
