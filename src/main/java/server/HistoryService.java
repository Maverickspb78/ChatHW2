package server;

import java.util.List;

public interface HistoryService {

    void save(List<String> chat);

    List<String> load();

}