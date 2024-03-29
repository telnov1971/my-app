package ru.omel.po.views.support;

import ru.omel.po.data.entity.Demand;
import ru.omel.po.data.entity.History;
import ru.omel.po.data.service.HistoryService;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HistoryLayout extends VerticalLayout {
    private List<History> historyList;
    private final HistoryService historyService;

    public HistoryLayout(HistoryService historyService) {
        this.historyService = historyService;
        historyList = new ArrayList<>();
    }

    public void findAllByDemand(Demand demand) {
        historyList = historyService.findAllByDemand(demand);
        for(History history : historyList){
            HorizontalLayout oneHistory = new HorizontalLayout();
            oneHistory.setWidthFull();
            Label labelCreateDate = new Label(history.getCreateDate()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd | HH:mm")));
            labelCreateDate.setWidth("10em");
            Label labelClient = new Label("");
            switch (history.getClient()) {
                case 0 -> labelClient.setText("Омскэлектро");
                case 1 -> labelClient.setText("Клиент");
                case 2 -> labelClient.setText("ГП");
                default -> labelClient.setText("");
            }
            labelClient.setMinWidth("6em");
            TextArea textHistory = new TextArea();
            textHistory.setValue(history.getHistory());
            textHistory.setWidthFull();
            textHistory.setReadOnly(true);
            oneHistory.add(labelCreateDate,labelClient,textHistory);
            add(oneHistory);
        }
    }
}
