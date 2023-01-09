package com.example.application.views.category;

import com.example.application.data.entity.Categories;
import com.example.application.data.service.CategoriesService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Category")
@Route(value = "category/:categoriesID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@Tag("category-view")
@JsModule("./views/category/category-view.ts")
public class CategoryView extends LitTemplate implements HasStyle, BeforeEnterObserver {

    private final String CATEGORIES_ID = "categoriesID";
    private final String CATEGORIES_EDIT_ROUTE_TEMPLATE = "category/%s/edit";

    // This is the Java companion file of a design
    // You can find the design file inside /frontend/views/
    // The design can be easily edited by using Vaadin Designer
    // (vaadin.com/designer)

    @Id
    private Grid<Categories> grid;

    @Id
    private TextField idCategories;
    @Id
    private TextField namaCategories;

    @Id
    private Button cancel;
    @Id
    private Button save;

    private BeanValidationBinder<Categories> binder;

    private Categories categories;

    private final CategoriesService categoriesService;

    public CategoryView(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
        addClassNames("category-view");
        grid.addColumn(Categories::getIdCategories).setHeader("Id Categories").setAutoWidth(true);
        grid.addColumn(Categories::getNamaCategories).setHeader("Nama Categories").setAutoWidth(true);
        grid.setItems(query -> categoriesService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(CATEGORIES_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(CategoryView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Categories.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(idCategories).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("idCategories");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.categories == null) {
                    this.categories = new Categories();
                }
                binder.writeBean(this.categories);
                categoriesService.update(this.categories);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(CategoryView.class);
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
        Optional<Long> categoriesId = event.getRouteParameters().get(CATEGORIES_ID).map(Long::parseLong);
        if (categoriesId.isPresent()) {
            Optional<Categories> categoriesFromBackend = categoriesService.get(categoriesId.get());
            if (categoriesFromBackend.isPresent()) {
                populateForm(categoriesFromBackend.get());
            } else {
                Notification.show(String.format("The requested categories was not found, ID = %s", categoriesId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(CategoryView.class);
            }
        }
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Categories value) {
        this.categories = value;
        binder.readBean(this.categories);

    }
}
