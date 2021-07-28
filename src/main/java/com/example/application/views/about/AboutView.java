package com.example.application.views.about;

import com.example.application.data.entity.Demand;
import com.example.application.data.service.DemandRepository;
import com.example.application.data.service.UserRepository;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.example.application.views.main.MainView;
import org.apache.commons.lang3.RandomUtils;

@Route(value = "about", layout = MainView.class)
@PageTitle("About")
public class AboutView extends Div {

    public AboutView(UserRepository userRepository, DemandRepository demandRepository) {
        addClassName("about-view");
        add(new Text("Data generated"));
        String listDemander[] = {"Ivanov","Petrov","Sidorov","Kozlov","Kalashnikov",
                "Kuznetsov","Semenov","Savaliev","Arbatov","Nemo"};
        String listIssued[] = {"Moskow","Omsk","Piter","Sochi","Tambov",
                "Anapa","Khabarovsk","Krasnoyarsk","Ufa","Tumen"};
        String listObject[] = {"House","Home","Office","Enterprise","Factory","Hospital","Restaurant"};
        String listNumber[] = {"123456","654321","153624","452163","145236"};
        String listSeries[] = {"1234","6543","1536","4521","1452"};

        for (int i = 0; i < 100; i++) {
            Demand demand = new Demand();
            demand.setDemander(listDemander[RandomUtils.nextInt(0,9)]);
            demand.setPassportSerries(listSeries[RandomUtils.nextInt(0,4)]);
            demand.setPassportNumber(listNumber[RandomUtils.nextInt(0,4)]);
            demand.setPasportIssued(listIssued[RandomUtils.nextInt(0,9)]);
            demand.setAddressActual(listIssued[RandomUtils.nextInt(0,9)]);
            demand.setObject(listObject[RandomUtils.nextInt(0,6)]);
            demand.setAddress(listIssued[RandomUtils.nextInt(0,9)]);
            demand.setUser(userRepository.findById(1L).get());
            demandRepository.save(demand);
        }
    }
}
