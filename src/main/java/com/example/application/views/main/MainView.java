package com.example.application.views.main;

import com.example.application.data.entity.Role;
import com.example.application.data.entity.User;
import com.example.application.data.service.UserService;
import com.example.application.views.demandedit.DemandEditTemporal;
import com.example.application.views.demandedit.DemandEditTo15;
import com.example.application.views.demandedit.DemandEditTo150;
import com.example.application.views.demandedit.DemandEditeGeneral;
import com.example.application.views.demandlist.DemandList;
import com.example.application.views.users.Profile;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.*;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * The main view is a top-level placeholder for other views.
 */
//@PWA(name = "Личный кабинет", shortName = "Кабинет")
public class MainView extends AppLayout {
    @Value("${upload.path.windows}")
    private String uploadPathWindows;
    @Value("${upload.path.linux}")
    private String uploadPathLinux;
    private H1 viewTitle;
    private Label newDemands = new Label("Новые заявки:");
    private OrderedList list = new OrderedList();

    private final UserService userService;
    private Role role = Role.ANONYMOUS;

    public static class MenuItemInfo {

        private String text;
        private String iconClass;
        private Class<? extends Component> view;

        public MenuItemInfo(String text, String iconClass, Class<? extends Component> view) {
            this.text = text;
            this.iconClass = iconClass;
            this.view = view;
        }

        public String getText() {
            return text;
        }

        public String getIconClass() {
            return iconClass;
        }

        public Class<? extends Component> getView() {
            return view;
        }

    }

    public MainView(UserService userService) {
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        addToDrawer(createDrawerContent());
        this.userService = userService;
    }

    private Component createHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassName("text-secondary");
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", "Menu toggle");
        toggle.getElement().setAttribute("title","Меню");

        viewTitle = new H1();
        viewTitle.addClassNames("m-0", "text-l");

        Header header = new Header(toggle, viewTitle);
        header.addClassNames("bg-base", "border-b", "border-contrast-10", "box-border", "flex", "h-xl", "items-center",
                "w-full");
        return header;
    }

    private Component createDrawerContent() {
        HorizontalLayout main = new HorizontalLayout();
        Image logo = new Image("images/logo.png", "Омскэлектро");
        logo.setMaxWidth("20%");
        logo.getElement().getStyle().set("padding", "10px");
        H2 appName = new H2("Личный кабинет");
        main.add(logo,appName);
        appName.addClassNames("flex", "items-center", "h-xl", "m-0", "px-m", "text-m");

        com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(
                main, createNavigation(), createFooter());
        section.addClassNames("flex", "flex-col", "items-stretch", "max-h-full", "min-h-full");
        return section;
    }

    private Nav createNavigation() {
        Nav nav = new Nav();
        nav.addClassNames("border-b", "border-contrast-10", "flex-grow", "overflow-auto");
        nav.getElement().setAttribute("aria-labelledby", "views");

        // Wrap the links in a list; improves accessibility
        list.addClassNames("list-none", "m-0", "p-0");

        nav.add(createLinksList());
        newDemands.getElement().getStyle().set("margin", "10px");
        newDemands.getElement().getStyle().set("text-decoration", "underline");
        nav.add(newDemands);
        nav.add(list);
        for (RouterLink link : createLinks()) {
            ListItem item = new ListItem(link);
            list.add(item);
        }
        Label profile = new Label("Пользователь:");
        profile.getElement().getStyle().set("margin", "10px");
        profile.getElement().getStyle().set("text-decoration", "underline");
        nav.add(profile);
        nav.add(createLinksProfile());
        Button button = new Button(new Icon(VaadinIcon.EXIT));
        button.addClickListener(event -> {
            if(getUI().isPresent()){
                UI ui = getUI().get();
                ui.getSession().getSession().invalidate();
                ui.navigate("/");
            }
        });
        button.getElement().setAttribute("title","Выход их личного кабинета");
        button.setText("ВЫХОД");
        button.getElement().getStyle().set("margin", "10px");
        nav.add(button);
        return nav;
    }

    private List<RouterLink> createLinks() {
        MenuItemInfo[] menuItems = new MenuItemInfo[]{ //
//                new MenuItemInfo("Список заявок", "la la-globe", DemandList.class), //

                new MenuItemInfo("Физические лица до 15 кВт (ком.-быт. нужды)"
                        , "la la-file", DemandEditTo15.class), //
                new MenuItemInfo("Юридические лица и ИП до 150кВт (один источник электропитания)"
                        , "la la-file", DemandEditTo150.class), //
                new MenuItemInfo("Временное присоединение", "la la-file", DemandEditTemporal.class), //
                new MenuItemInfo("Иные категории потребителей", "la la-file", DemandEditeGeneral.class) //

//                new MenuItemInfo("Профиль", "la la-file", Profile.class), //

        };
        List<RouterLink> links = new ArrayList<>();
        for (MenuItemInfo menuItemInfo : menuItems) {
            RouterLink link = createLink(menuItemInfo);
            links.add(link);
        }
        return links;
    }

    private RouterLink createLinksList() {
        MenuItemInfo menuItem = new MenuItemInfo("Список заявок", "globe-solid", DemandList.class);
        return createLink(menuItem);
    }

    private RouterLink createLinksProfile() {
        MenuItemInfo menuItem = new MenuItemInfo("Регистрационные данные", "la la-globe", Profile.class);
        return createLink(menuItem);
    }

    private static RouterLink createLink(MenuItemInfo menuItemInfo) {
        RouterLink link = new RouterLink();
        link.addClassNames("flex", "mx-s", "p-s", "relative", "text-secondary");
        link.setRoute(menuItemInfo.getView());

        Span icon = new Span();
        icon.addClassNames("me-s", "text-l");
        if (!menuItemInfo.getIconClass().isEmpty()) {
            icon.addClassNames(menuItemInfo.getIconClass());
        }

        Span text = new Span(menuItemInfo.getText());
        text.addClassNames("font-medium", "text-s");

        link.add(icon, text);
        return link;
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.addClassNames("flex", "items-center", "my-s", "px-m", "py-xs");

        Label support = new Label("По вопросам заполнения Заявок звонить по тел.: 53-81-89. " +
                "При возникновении ошибок и сбоев в работе звонить по тел. 53-81-65 " +
                "или присылать сообщения на e-mail support@omskelectro.ru");
        support.addClassNames("flex", "mx-s", "p-s", "relative", "text-secondary");
        support.getElement().getStyle().set("font-size","0.8em");
        layout.add(support);

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
        User currentUser = userService.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName());
        if(currentUser != null) {
            role = currentUser.getRoles().contains(Role.USER) ?
                    Role.USER :
                    currentUser.getRoles().contains(Role.GARANT) ?
                            Role.GARANT :
                            currentUser.getRoles().contains(Role.ADMIN) ?
                                    Role.ADMIN :
                                    Role.ANONYMOUS;
        } else {
            role = Role.ANONYMOUS;
        }
        switch(role){
            case ANONYMOUS:
            case GARANT:
                newDemands.setVisible(false);
                list.setVisible(false);
            break;
        }
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
