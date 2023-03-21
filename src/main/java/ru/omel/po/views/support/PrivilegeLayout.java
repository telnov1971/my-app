package ru.omel.po.views.support;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import ru.omel.po.data.entity.Demand;
import ru.omel.po.data.entity.Privilege;
import ru.omel.po.data.service.HistoryService;
import ru.omel.po.data.service.PrivilegeService;

import static ru.omel.po.views.support.PrivilegeLayout.PrivilegeState.*;

public class PrivilegeLayout extends VerticalLayout {
    public enum PrivilegeState {SET,NOTSET,CHANGE,NOTCHANGE}
    private final PrivilegeService privilegeService;
    private final HistoryService historyService;
    private final GeneralForm formParent;
    private Privilege privilege = new Privilege();
    private Privilege privilegeOld = new Privilege();
//    private Boolean needy = false;
//    private Boolean veteran = false;
//    private Boolean invalid = false;
//    private Boolean chernobyl = false;
//    private Boolean semipalatinsk = false;
//    private Boolean lawmaker = false;
//    private Boolean lighthouse = false;
//    private Boolean chernobylRisk = false;
//    private Boolean manyChildren = false;

    private final Checkbox needy = new Checkbox();
    private final Checkbox veteran = new Checkbox();
    private final Checkbox invalid = new Checkbox();
    private final Checkbox chernobyl = new Checkbox();
    private final Checkbox semipalatinsk = new Checkbox();
    private final Checkbox lawmaker = new Checkbox();
    private final Checkbox lighthouse = new Checkbox();
    private final Checkbox chernobylRisk = new Checkbox();
    private final Checkbox manyChildren = new Checkbox();

    private final Binder<Privilege> binderPrivilege = new Binder<>(Privilege.class);

    public PrivilegeLayout(PrivilegeService privilegeService
            , HistoryService historyService
            , GeneralForm formParent) {
        this.privilegeService = privilegeService;
        this.historyService = historyService;
        this.formParent = formParent;

        HorizontalLayout horizontal1 = new HorizontalLayout();
        HorizontalLayout horizontal2 = new HorizontalLayout();
        HorizontalLayout horizontal3 = new HorizontalLayout();
        HorizontalLayout horizontal4 = new HorizontalLayout();
        HorizontalLayout horizontal5 = new HorizontalLayout();
        HorizontalLayout horizontal6 = new HorizontalLayout();
        HorizontalLayout horizontal7 = new HorizontalLayout();
        HorizontalLayout horizontal8 = new HorizontalLayout();
        HorizontalLayout horizontal9 = new HorizontalLayout();
        Label labelNeedy = new Label("член малоимущей семьи (одиноко проживающий гражданин),"
                +" среднедушевой доход которого ниже величины прожиточного минимума, установленного"
                +" в соответствующем субъекте Российской Федерации, определенным в соответствии с"
                +" Федеральным законом \"О прожиточном минимуме в Российской Федерации\"");
        Label labelVeteran = new Label("лицо указанное в статьях 14 - 16, 18 и 21 Федерального"
                +" закона \"О ветеранах\"");
        Label labelInvalid = new Label("лицо указанное в статье 17 Федерального закона"
                +" \"О социальной защите инвалидов в Российской Федерации\"");
        Label labelChernobyl = new Label("лицо указанное в статье 14 Закона Российской Федерации"
                +" \"О социальной защите граждан, подвергшихся воздействию радиации вследствие"
                +" катастрофы на Чернобыльской АЭС\"");
        Label labelSemipalatinsk = new Label("лицо указанное в статье 2 Федерального закона"
                +" \"О социальных гарантиях гражданам, подвергшимся радиационному воздействию"
                +" вследствие ядерных испытаний на Семипалатинском полигоне\"");
        Label labelLawmaker = new Label("лицо указанное в части 8 статьи 154 Федерального закона"
                +" \"О внесении изменений в законодательные акты Российской Федерации и признании"
                +" утратившими силу некоторых законодательных актов Российской Федерации в связи с"
                +" принятием федеральных законов \"О внесении изменений и дополнений в Федеральный"
                +" закон \"Об общих принципах организации законодательных (представительных) и"
                +" исполнительных органов государственной власти субъектов Российской Федерации\" и"
                +" \"Об общих принципах организации местного самоуправления в Российской Федерации\"");
        Label labelLighthouse = new Label("лицо указанное в статье 1 Федерального закона \"О"
                +" социальной защите граждан Российской Федерации, подвергшихся воздействию радиации"
                +" вследствие аварии в 1957 году на производственном объединении \"Маяк\" и сбросов"
                +" радиоактивных отходов в реку Теча\"");
        Label labelChernobylRisk = new Label("лицо указанное в пункте 1 и абзаце четвертом пункта"
                +" 2 постановления Верховного Совета Российской Федерации от 27 декабря 1991 г. N 2123-1"
                +" \"О распространении действия Закона РСФСР \"О социальной защите граждан, подвергшихся"
                +" воздействию радиации вследствие катастрофы на Чернобыльской АЭС\" на граждан из"
                +" подразделений особого риска\"");
        Label labelManyChildren = new Label("лицо указанное в Указе Президента Российской Федерации"
                +" от 5 мая 1992 г. N 431 \"О мерах по социальной поддержке многодетных семей\"");
        horizontal1.add(needy,labelNeedy);
        horizontal2.add(veteran,labelVeteran);
        horizontal3.add(invalid,labelInvalid);
        horizontal4.add(chernobyl,labelChernobyl);
        horizontal5.add(semipalatinsk,labelSemipalatinsk);
        horizontal6.add(lawmaker,labelLawmaker);
        horizontal7.add(lighthouse,labelLighthouse);
        horizontal8.add(chernobylRisk,labelChernobylRisk);
        horizontal9.add(manyChildren,labelManyChildren);
        add(horizontal1,horizontal2,horizontal3,horizontal4,horizontal5
                ,horizontal6,horizontal7,horizontal8,horizontal9);
        setListeners();
//        checkbox.setLabel("I accept the terms and conditions");
        binderPrivilege.bindInstanceFields(this);
    }

    private void setListeners() {
        needy.addValueChangeListener(e->{
            if(needy.getValue()) formParent.setPrivilegeNot();
        });
        veteran.addValueChangeListener(e->{
            if(veteran.getValue()) formParent.setPrivilegeNot();
        });
        invalid.addValueChangeListener(e->{
            if(invalid.getValue()) formParent.setPrivilegeNot();
        });
        chernobyl.addValueChangeListener(e->{
            if(chernobyl.getValue()) formParent.setPrivilegeNot();
        });
        semipalatinsk.addValueChangeListener(e->{
            if(semipalatinsk.getValue()) formParent.setPrivilegeNot();
        });
        lawmaker.addValueChangeListener(e->{
            if(lawmaker.getValue()) formParent.setPrivilegeNot();
        });
        lighthouse.addValueChangeListener(e->{
            if(lighthouse.getValue()) formParent.setPrivilegeNot();
        });
        chernobylRisk.addValueChangeListener(e->{
            if(chernobylRisk.getValue()) formParent.setPrivilegeNot();
        });
        manyChildren.addValueChangeListener(e->{
            if(manyChildren.getValue()) formParent.setPrivilegeNot();
        });
    }

    public void setDemand(Demand demand) {
        this.privilege = privilegeService.findByDemand(demand);
//        this.privilegeOld = this.privilege;
        if(privilege==null){
            privilege = new Privilege();
            privilege.setDemand(demand);
        }
//        binderPrivilege.bindInstanceFields(this);
        binderPrivilege.readBean(privilege);

    }

    public void savePrivilege(Demand demand) {
        binderPrivilege.writeBeanIfValid(privilege);
        privilege.setDemand(demand);
        privilegeService.update(privilege);
    }

    public PrivilegeState getPrivilege(Demand demand) {
        binderPrivilege.writeBeanIfValid(privilege);
        if(demand.isPrivilege() != getPrivilegeStatus()) {
            if(getPrivilegeStatus())
                return SET;
            else
                return NOTSET;
        } else {
            privilegeOld = privilegeService.findByDemand(demand);
            if(privilegeOld != null) {
                if (privilegeOld.equals(privilege))
                    return NOTCHANGE;
                else
                    return CHANGE;
            }
        }
        return NOTCHANGE;
    }
    public boolean getPrivilegeStatus(){
        return needy.getValue() || veteran.getValue()
                || chernobyl.getValue() || invalid.getValue()
                || semipalatinsk.getValue() || lawmaker.getValue()
                || lighthouse.getValue() || chernobylRisk.getValue()
                || manyChildren.getValue();
    }

    public void setReadOnly(boolean readOnly){
        needy.setReadOnly(readOnly);
        veteran.setReadOnly(readOnly);
        chernobyl.setReadOnly(readOnly);
        invalid.setReadOnly(readOnly);
        semipalatinsk.setReadOnly(readOnly);
        lawmaker.setReadOnly(readOnly);
        lighthouse.setReadOnly(readOnly);
        chernobylRisk.setReadOnly(readOnly);
        manyChildren.setReadOnly(readOnly);
    }
    public void setValueFalse(){
        needy.setValue(false);
        veteran.setValue(false);
        chernobyl.setValue(false);
        invalid.setValue(false);
        semipalatinsk.setValue(false);
        lawmaker.setValue(false);
        lighthouse.setValue(false);
        chernobylRisk.setValue(false);
        manyChildren.setValue(false);
    }
}
