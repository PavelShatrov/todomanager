package org.yournamehere.client;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import java.util.Date;
import java.util.List;

/**
 * Main entry point.
 *
 * @author Pavel Shatrov
 */
public class MainEntryPoint implements EntryPoint {

    static class TODOItemW extends JavaScriptObject {

        protected TODOItemW() {
        }

        public final native String getDescription() /*-{ return this.description; }-*/;

        public final native int getDate() /*-{ return this.date; }-*/;

        public final native int getId() /*-{ return this.id; }-*/;

        public final native boolean getIsDone() /*-{ return this.isDone; }-*/;

    }

    private class TODOItem {

        private String str;
        private Date date;
        private int id;
        private boolean isDone;

        public TODOItem(String str, Date date, int id, boolean isDone) {
            this.str = str;
            this.date = date;
            this.id = id;
            this.isDone = isDone;
        }

        public String getStr() {
            return str;
        }

        public Date getDate() {
            return date;
        }

        public int getId() {
            return id;
        }

        public boolean isIsDone() {
            return isDone;
        }

    }

    /**
     * Creates a new instance of MainEntryPoint
     */
    public MainEntryPoint() {
    }

    /**
     * The entry point method, called automatically by loading a module that
     * declares an implementing class as an entry-point
     */
    @Override
    public void onModuleLoad() {
        final ListDataProvider<TODOItem> dataProvider = new ListDataProvider<>();
        final List<TODOItem> list = dataProvider.getList();
        final CellTable<TODOItem> table = new CellTable<>();

        final DialogBox dialogBox = new DialogBox();
        dialogBox.setGlassEnabled(true);
        dialogBox.setAnimationEnabled(true);
        dialogBox.setText("Add new item");
        VerticalPanel dialogContents = new VerticalPanel();
        dialogContents.setSpacing(4);

        final TextBox editBox = new TextBox();
        dialogContents.add(editBox);
        Button closeDialog = new Button("Save");
        closeDialog.addClickHandler((event) -> {

            RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, "http://localhost:8090/todo/save");
            builder.setHeader("content-type", "application/json");
            builder.setHeader("Access-Control-Allow-Origin", "*");
            try {
                JSONObject data = new JSONObject();

                data.put("description", new JSONString(editBox.getText()));
                data.put("isDone", JSONBoolean.getInstance(false));

                Request response = builder.sendRequest(data.toString(), new RequestCallback() {
                    @Override
                    public void onResponseReceived(Request request, Response response) {
                        int statusCode = response.getStatusCode();

                        if (statusCode == Response.SC_OK) {
                            TODOItemW added = JsonUtils.<TODOItemW>safeEval(response.getText());
                            list.add(new TODOItem(added.getDescription(), new Date(added.getDate()), added.getId(), added.getIsDone()));
                            editBox.setText("");
                        } else {
                        }
                    }

                    @Override
                    public void onError(Request request, Throwable exception) {

                    }
                });
            } catch (RequestException ex) {
                System.err.println("");
            }
            dialogBox.hide();
        });
        Button cancelButton = new Button("Cancel");
        closeDialog.setStyleName("paddedMenuPanel");
        cancelButton.addClickHandler((event) -> {
            dialogBox.hide();
        });
        FlowPanel dPanel = new FlowPanel();
        dPanel.add(closeDialog);
        dPanel.add(cancelButton);
        dialogContents.add(dPanel);
        dialogContents.setHeight("300");
        dialogContents.setWidth("500");
        dialogContents.setSpacing(10);
        dialogContents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        dialogBox.setWidget(dialogContents);

        TextColumn<TODOItem> strColumn
                = new TextColumn<TODOItem>() {
            @Override
            public String getValue(TODOItem object) {
                return object.getStr();
            }
        };
        DateCell dateCell = new DateCell(DateTimeFormat.getFormat("MM/dd/yyyy HH:mm"));
        Column<TODOItem, Date> dateColumn = new Column<TODOItem, Date>(dateCell) {
            @Override
            public Date getValue(TODOItem object) {
                return object.getDate();
            }
        };

        TextColumn<TODOItem> doneColumn = new TextColumn<TODOItem>() {
            @Override
            public String getValue(TODOItem object) {
                return object.isIsDone() ? "DONE" : "NOT COMPLETE";
            }
        };

        table.addColumn(strColumn, "Description");
        table.addColumn(dateColumn, "Created");
        table.addColumn(doneColumn, "Completed");

        final SelectionModel<TODOItem> selectionModel = new SingleSelectionModel<>();

        table.setSelectionModel(selectionModel, (event) -> {

            System.err.println("Select");
        });

        Column<TODOItem, Boolean> checkColumn = new Column<TODOItem, Boolean>(
                new CheckboxCell(true, false)) {
            @Override
            public Boolean getValue(TODOItem object) {

                return selectionModel.isSelected(object);
            }
        };
        checkColumn.setFieldUpdater((int index, TODOItem object, Boolean value) -> {
            selectionModel.setSelected(object, value);
            dataProvider.refresh();
        });
        table.addColumn(checkColumn);

        dataProvider.addDataDisplay(table);

        VerticalPanel mainPanel = new VerticalPanel();
        FlowPanel menuPanel = new FlowPanel();

        Button addButton = new Button("Add");
        addButton.setStyleName("paddedMenuPanel");
        addButton.addClickHandler((event) -> {
            dialogBox.center();
            dialogBox.show();
        });
        menuPanel.add(addButton);
        Button deleteButton = new Button("Delete");
        deleteButton.addClickHandler((event) -> {
            final TODOItem selected = (TODOItem) ((SingleSelectionModel) table.getSelectionModel()).getSelectedObject();
            if (selected == null) {
                return;
            }
            RequestBuilder builder = new RequestBuilder(RequestBuilder.DELETE, "http://localhost:8090/todo/" + selected.getId());
            builder.setHeader("content-type", "application/json");
            builder.setHeader("Access-Control-Allow-Origin", "*");
            try {
                Request response = builder.sendRequest("", new RequestCallback() {
                    @Override
                    public void onResponseReceived(Request request, Response response) {
                        int statusCode = response.getStatusCode();
                        if (statusCode == Response.SC_OK) {
                            list.remove(selected);
                        } else {
                        }
                    }

                    @Override
                    public void onError(Request request, Throwable exception) {

                    }
                });
            } catch (RequestException ex) {
                System.err.println(ex.toString());
            }
        });
        deleteButton.setStyleName("paddedMenuPanel");
        menuPanel.add(deleteButton);
        final DialogBox editdBox = new DialogBox();
        final TextBox editField = new TextBox();
        Button editButton = new Button("Edit");
        editButton.addClickHandler((ClickEvent event) -> {
            final TODOItem selected = (TODOItem) ((SingleSelectionModel) table.getSelectionModel()).getSelectedObject();
            if (selected == null) {
                return;
            }
            editField.setText(selected.getStr());
            editdBox.center();
            editdBox.show();
        });

        editdBox.setGlassEnabled(true);
        editdBox.setAnimationEnabled(true);
        editdBox.setText("Edit item");
        VerticalPanel editContents = new VerticalPanel();
        editContents.setSpacing(4);

        editContents.add(editField);
        Button closedDialog = new Button("Save");
        closedDialog.setStyleName("paddedMenuPanel");
        Button canceldButton = new Button("Cancel");
        canceldButton.addClickHandler((event) -> {
            editdBox.hide();
        });
        
        FlowPanel editdPanel = new FlowPanel();
        editdPanel.add(closedDialog);
        editdPanel.add(canceldButton);
        editContents.add(editdPanel);
        editContents.setHeight("300");
        editContents.setWidth("500");
        editContents.setSpacing(10);
        editContents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        editdBox.setWidget(editContents);
        closedDialog.addClickHandler((event) -> {
            final TODOItem selected = (TODOItem) ((SingleSelectionModel) table.getSelectionModel()).getSelectedObject();
            RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, "http://localhost:8090/todo/update/" + selected.getId());
            builder.setHeader("content-type", "application/json");
            builder.setHeader("Access-Control-Allow-Origin", "*");
            try {
                JSONObject data = new JSONObject();
                data.put("description", new JSONString(editField.getText()));
                data.put("isDone", JSONBoolean.getInstance(selected.isIsDone()));

                Request response = builder.sendRequest(data.toString(), new RequestCallback() {
                    @Override
                    public void onResponseReceived(Request request, Response response) {
                        int statusCode = response.getStatusCode();

                        if (statusCode == Response.SC_OK) {
                            TODOItemW added = JsonUtils.<TODOItemW>safeEval(response.getText());
                            int idx = list.indexOf(selected);
                            list.set(idx, new TODOItem(added.getDescription(), new Date(added.getDate()), added.getId(), added.getIsDone()));
                        } else {
                        }
                    }

                    @Override
                    public void onError(Request request, Throwable exception) {

                    }
                });
            } catch (RequestException ex) {
                System.err.println("");
            }
            editdBox.hide();
        });

        editButton.setStyleName("paddedMenuPanel");
        menuPanel.add(editButton);
        Button doneButton = new Button("Mark DONE");

        doneButton.addClickHandler((ClickEvent event) -> {
            final TODOItem selected = (TODOItem) ((SingleSelectionModel) table.getSelectionModel()).getSelectedObject();
            if (selected == null) {
                return;
            }
            RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, "http://localhost:8090/todo/update/" + selected.getId());
            builder.setHeader("content-type", "application/json");
            builder.setHeader("Access-Control-Allow-Origin", "*");
            try {
                JSONObject data = new JSONObject();
                data.put("description", new JSONString(selected.getStr()));
                data.put("isDone", JSONBoolean.getInstance(true));

                Request response = builder.sendRequest(data.toString(), new RequestCallback() {
                    @Override
                    public void onResponseReceived(Request request, Response response) {
                        int statusCode = response.getStatusCode();

                        if (statusCode == Response.SC_OK) {
                            TODOItemW added = JsonUtils.<TODOItemW>safeEval(response.getText());
                            int idx = list.indexOf(selected);
                            list.set(idx, new TODOItem(added.getDescription(), new Date(added.getDate()), added.getId(), added.getIsDone()));
                        } else {
                        }
                    }

                    @Override
                    public void onError(Request request, Throwable exception) {

                    }
                });
            } catch (RequestException ex) {
                System.err.println("");
            }
        });

        doneButton.setStyleName("paddedMenuPanel");
        menuPanel.add(doneButton);
        mainPanel.add(menuPanel);
        mainPanel.add(table);

        mainPanel.addAttachHandler((event) -> {
            RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "http://localhost:8090/todo");
            builder.setHeader("content-type", "application/json");
            builder.setHeader("Access-Control-Allow-Origin", "*");
            try {
                Request response = builder.sendRequest("", new RequestCallback() {
                    @Override
                    public void onResponseReceived(Request request, Response response) {
                        int statusCode = response.getStatusCode();

                        if (statusCode == Response.SC_OK) {

                            JsArray<TODOItemW> array = JsonUtils.<JsArray<TODOItemW>>safeEval(response.getText());
                            for (int i = 0; i < array.length(); i++) {
                                list.add(new TODOItem(array.get(i).getDescription(), new Date(array.get(i).getDate()), array.get(i).getId(), array.get(i).getIsDone()));
                            }
                        } else {
                        }
                    }

                    @Override
                    public void onError(Request request, Throwable exception) {

                    }
                });
            } catch (RequestException ex) {
                System.err.println(ex.toString());
            }
        });

        final RootPanel panel = RootPanel.get();
        panel.add(mainPanel);
    }
}
