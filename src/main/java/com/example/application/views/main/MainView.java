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
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

import static com.example.application.data.entity.Role.ANONYMOUS;
import static com.example.application.data.entity.Role.USER;

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
    Label newDemands = new Label("Новые заявки:");
    OrderedList list = new OrderedList();

    private final UserService userService;

    private Role role = Role.ANONYMOUS;
    //private final Tabs menu;
    private final MenuBar menuBar = new MenuBar();

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
//        VaadinSession.getCurrent().addRequestHandler(
//                new RequestHandler() {
//                    @Override
//                    public boolean handleRequest(VaadinSession session,
//                                              VaadinRequest request,
//                                              VaadinResponse response)
//                            throws IOException {
//                        if (request.getPathInfo().contains("/files/")) {
//                            response.setContentType("text/plain");
//                            String uploadPath = new String();
//                            String osName = System.getProperty("os.name");
//                            if(osName.contains("Windows")) uploadPath = uploadPathWindows;
//                            if(osName.contains("Linux")) uploadPath = uploadPathLinux;
//
//                            String filename = request.getPathInfo().substring(7);
//                            //response.setContentType("application/octet-stream");
//                            File file = new File(uploadPath + filename);
//                            InputStream inputStream = new FileInputStream(file);
//                            response.getOutputStream().write(inputStream.readAllBytes());
//                            return true;
//                        }
//                        return false;
//                    }
//                }
//        );

        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        addToDrawer(createDrawerContent());

        this.userService = userService;
//        HorizontalLayout header = createHeader();

        //menu = createMenuTabs();
//        menuBar.setOpenOnHover(true);
//        createMenuBar(menuBar);
//        addToNavbar(createTopMenuBar(header, menuBar));
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
        Image logo = new Image("images/logo.png", "Омскэлектро");
        H2 appName = new H2("Личный кабинет");
        appName.addClassNames("flex", "items-center", "h-xl", "m-0", "px-m", "text-m");

        com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(appName,
                createNavigation(), createFooter());
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
                ui.navigate("/login");
            }
        });
        button.getElement().setAttribute("title","Выход их личного кабинета");
        nav.add(button);
        return nav;
    }

    private List<RouterLink> createLinks() {
        MenuItemInfo[] menuItems = new MenuItemInfo[]{ //
//                new MenuItemInfo("Список заявок", "la la-globe", DemandList.class), //

                new MenuItemInfo("Физические лица до 15 кВт (ком.-быт. нужды)", "la la-file", DemandEditTo15.class), //
                new MenuItemInfo("Юридические лица и ИП до 150кВт (один источник электропитания)", "la la-file", DemandEditTo150.class), //
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
        RouterLink link = createLink(menuItem);
        return link;
    }

    private RouterLink createLinksProfile() {
        MenuItemInfo menuItem = new MenuItemInfo("Регистрационные данные", "la la-globe", Profile.class);
        RouterLink link = createLink(menuItem);
        return link;
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

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
        User user = userService.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName());
        if(user != null) {
            role = user.getRoles().contains(Role.USER) ?
                    Role.USER :
                    user.getRoles().contains(Role.GARANT) ?
                            Role.GARANT :
                            user.getRoles().contains(Role.ADMIN) ?
                                    Role.ADMIN :
                                    Role.ANONYMOUS;
        } else {
            role = Role.ANONYMOUS;
        }//getTabForComponent(getContent()).ifPresent(menu::setSelectedTab);
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

    //=========================================================================
    // Вариант простого заголовка с меню
//    private void createMenuBar(MenuBar menuBar) {
//        menuBar.addItem("Список заявок", e ->{
//            UI.getCurrent().navigate(DemandList.class);
//        });
//        MenuItem editors = menuBar.addItem("Новая заявка");
//        editors.getSubMenu().addItem("Физические лица до 15 кВт", e -> {
//            UI.getCurrent().navigate(DemandEditTo15.class);
//        } );
//        editors.getSubMenu().addItem("Юридические лица и ИП до 150кВт", e -> {
//            UI.getCurrent().navigate(DemandEditTo150.class);
//        } );
//        editors.getSubMenu().addItem("Временное присоединение", e -> {
//            UI.getCurrent().navigate(DemandEditTemporal.class);
//        } );
//        editors.getSubMenu().addItem("Иные категории потребителей", e -> {
//            UI.getCurrent().navigate(DemandEditeGeneral.class);
//        } );
//        menuBar.addItem("Профиль", e ->{
//            String username =SecurityContextHolder.getContext().getAuthentication().getName();
//            User user = userService.findByUsername(username);
//            UI.getCurrent().navigate(Profile.class, new RouteParameters("userID",
//                    String.valueOf(user.getId())));
//        });
//    }
//
//    private VerticalLayout createTopBar(HorizontalLayout header, Tabs menu) {
//        VerticalLayout layout = new VerticalLayout();
//        layout.getThemeList().add("dark");
//        layout.setWidthFull();
//        layout.setSpacing(false);
//        layout.setPadding(false);
//        layout.setAlignItems(FlexComponent.Alignment.CENTER);
//        layout.add(header, menu);
//        return layout;
//    }
//
//    private VerticalLayout createTopMenuBar(HorizontalLayout header, MenuBar menu) {
//        VerticalLayout layout = new VerticalLayout();
//        layout.getThemeList().add("dark");
//        layout.setWidthFull();
//        layout.setSpacing(false);
//        layout.setPadding(false);
//        layout.setAlignItems(FlexComponent.Alignment.CENTER);
//        layout.add(header, menu);
//        return layout;
//    }
//
//    private HorizontalLayout createHeader() {
//        HorizontalLayout header = new HorizontalLayout();
//        header.setClassName("topmenu-header");
//        header.setPadding(false);
//        header.setSpacing(false);
//        header.setWidthFull();
//        header.setAlignItems(FlexComponent.Alignment.CENTER);
//        Image logo = new Image("images/logo.png", "Омскэлектро");
//        logo.setId("logo");
//        header.add(logo);
//        Avatar avatar = new Avatar();
//        avatar.setId("avatar");
//        header.add(new H1("Личный кабинет"));
//        header.add(avatar);
//        //Anchor logout = new Anchor("/logout", "Выход");
//        //header.add(logout);
//        Button button = new Button("Выход", event -> {
//            if(getUI().isPresent()){
//                UI ui = getUI().get();
//                ui.getSession().getSession().invalidate();
//                ui.navigate("/login");
//            }
//        });
//        header.add(button);
//        return header;
//    }
//
//    private static Tabs createMenuTabs() {
//        final Tabs tabs = new Tabs();
//        tabs.getStyle().set("max-width", "100%");
//        tabs.add(getAvailableTabs());
//        return tabs;
//    }
//
//    private static Tab[] getAvailableTabs() {
//        return new Tab[]{
//                //createTab("Master-Detail", MasterDetailView.class),
//                createTab("Список заявок", DemandList.class),
//                createTab("Заявка", DemandEditTo15.class)
//                //createTab("My App", MyAppView.class),
//                //createTab("About", AboutView.class)
//                };
//    }
//
//    private static Tab createTab(String text, Class<? extends Component> navigationTarget) {
//        final Tab tab = new Tab();
//        tab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
//        tab.add(new RouterLink(text, navigationTarget));
//        ComponentUtil.setData(tab, Class.class, navigationTarget);
//        return tab;
//    }

    /*
    private Optional<Tab> getTabForComponent(Component component) {
        return menu.getChildren().filter(tab -> ComponentUtil.getData(tab, Class.class).equals(component.getClass()))
                .findFirst().map(Tab.class::cast);
    }

     */
}
