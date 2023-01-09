package com.example.application.views.subcategory;

import com.example.application.data.entity.Subacetgory;
import com.example.application.data.service.SubacetgoryService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Subcategory")
@Route(value = "subcategory/:subacetgoryID?/:action?(edit)", layout = MainLayout.class)
public class SubcategoryView extends Div implements BeforeEnterObserver {

    private final String SUBACETGORY_ID = "subacetgoryID";
    private final String SUBACETGORY_EDIT_ROUTE_TEMPLATE = "subcategory/%s/edit";

    private final Grid<Subacetgory> grid = new Grid<>(Subacetgory.class, false);

    private TextField idSubcategories;
    private TextField namaSubcategories;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<Subacetgory> binder;

    private Subacetgory subacetgory;

    private final SubacetgoryService subacetgoryService;

    public SubcategoryView(SubacetgoryService subacetgoryService) {
        this.subacetgoryService = subacetgoryService;
        addClassNames("subcategory-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("idSubcategories").setAutoWidth(true);
        grid.addColumn("namaSubcategories").setAutoWidth(true);
        grid.setItems(query -> subacetgoryService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(SUBACETGORY_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(SubcategoryView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Subacetgory.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(idSubcategories).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("idSubcategories");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.subacetgory == null) {
                    this.subacetgory = new Subacetgory();
                }
                binder.writeBean(this.subacetgory);
                subacetgoryService.update(this.subacetgory);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(SubcategoryView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> subacetgoryId = event.getRouteParameters().get(SUBACETGORY_ID).map(Long::parseLong);
        if (subacetgoryId.isPresent()) {
            Optional<Subacetgory> subacetgoryFromBackend = subacetgoryService.get(subacetgoryId.get());
            if (subacetgoryFromBackend.isPresent()) {
                populateForm(subacetgoryFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested subacetgory was not found, ID = %s", subacetgoryId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(SubcategoryView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        idSubcategories = new TextField("Id Subcategories");
        namaSubcategories = new TextField("Nama Subcategories");
        formLayout.add(idSubcategories, namaSubcategories);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Subacetgory value) {
        this.subacetgory = value;
        binder.readBean(this.subacetgory);

    }
}
