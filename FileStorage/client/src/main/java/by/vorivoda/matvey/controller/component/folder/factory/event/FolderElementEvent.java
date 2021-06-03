package by.vorivoda.matvey.controller.component.folder.factory.event;

import javafx.event.Event;

public interface FolderElementEvent<T extends Event> {
    void handle(T event, String name);
}
