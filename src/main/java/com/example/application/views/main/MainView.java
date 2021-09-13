package com.example.application.views.main;

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
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * The main view is a top-level placeholder for other views.
 */
//@PWA(name = "Личный кабинет", shortName = "Кабинет")
public class MainView extends AppLayout {
    @Value("${upload.path.windows}")
    private String uploadPathWindows;
    @Value("${upload.path.linux}")
    private String uploadPathLinux;

    private final UserService userService;

    //private final Tabs menu;
    private final MenuBar menuBar = new MenuBar();

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

        this.userService = userService;
        HorizontalLayout header = createHeader();

        //menu = createMenuTabs();
        menuBar.setOpenOnHover(true);
        createMenuBar(menuBar);
        if(SecurityContextHolder
                .getContext().getAuthentication().getPrincipal() == "anonymousUser") {
            menuBar.setVisible(false);
        } else {
            menuBar.setVisible(true);
        }
        addToNavbar(createTopMenuBar(header, menuBar));
    }

    private void createMenuBar(MenuBar menuBar) {
        menuBar.addItem("Список заявок", e ->{
            UI.getCurrent().navigate(DemandList.class);
        });
        MenuItem editors = menuBar.addItem("Новая заявка");
        editors.getSubMenu().addItem("Физические лица до 15 кВт", e -> {
            UI.getCurrent().navigate(DemandEditTo15.class);
        } );
        editors.getSubMenu().addItem("Юридические лица и ИП до 150кВт", e -> {
            UI.getCurrent().navigate(DemandEditTo150.class);
        } );
        editors.getSubMenu().addItem("Временное присоединение", e -> {
            UI.getCurrent().navigate(DemandEditTemporal.class);
        } );
        editors.getSubMenu().addItem("Иные категории потребителей", e -> {
            UI.getCurrent().navigate(DemandEditeGeneral.class);
        } );
        menuBar.addItem("Профиль", e ->{
            String username =SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.findByUsername(username);
            UI.getCurrent().navigate(Profile.class, new RouteParameters("userID",
                    String.valueOf(user.getId())));
        });
    }

    private VerticalLayout createTopBar(HorizontalLayout header, Tabs menu) {
        VerticalLayout layout = new VerticalLayout();
        layout.getThemeList().add("dark");
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setPadding(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.add(header, menu);
        return layout;
    }

    private VerticalLayout createTopMenuBar(HorizontalLayout header, MenuBar menu) {
        VerticalLayout layout = new VerticalLayout();
        layout.getThemeList().add("dark");
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setPadding(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.add(header, menu);
        return layout;
    }

    private HorizontalLayout createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setClassName("topmenu-header");
        header.setPadding(false);
        header.setSpacing(false);
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        Image logo = new Image("images/logo.png", "Омскэлектро");
        logo.setId("logo");
        header.add(logo);
        Avatar avatar = new Avatar();
        avatar.setId("avatar");
        header.add(new H1("Личный кабинет"));
        header.add(avatar);
        //Anchor logout = new Anchor("/logout", "Выход");
        //header.add(logout);
        Button button = new Button("Выход", event -> {
            if(getUI().isPresent()){
                UI ui = getUI().get();
                ui.getSession().getSession().invalidate();
                ui.navigate("/login");
            }
        });
        header.add(button);
        return header;
    }

    private static Tabs createMenuTabs() {
        final Tabs tabs = new Tabs();
        tabs.getStyle().set("max-width", "100%");
        tabs.add(getAvailableTabs());
        return tabs;
    }

    private static Tab[] getAvailableTabs() {
        return new Tab[]{
                //createTab("Master-Detail", MasterDetailView.class),
                createTab("Список заявок", DemandList.class),
                createTab("Заявка", DemandEditTo15.class)
                //createTab("My App", MyAppView.class),
                //createTab("About", AboutView.class)
                };
    }

    private static Tab createTab(String text, Class<? extends Component> navigationTarget) {
        final Tab tab = new Tab();
        tab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
        tab.add(new RouterLink(text, navigationTarget));
        ComponentUtil.setData(tab, Class.class, navigationTarget);
        return tab;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        //getTabForComponent(getContent()).ifPresent(menu::setSelectedTab);
    }

    /*
    private Optional<Tab> getTabForComponent(Component component) {
        return menu.getChildren().filter(tab -> ComponentUtil.getData(tab, Class.class).equals(component.getClass()))
                .findFirst().map(Tab.class::cast);
    }

     */
}
