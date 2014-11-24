/*
 * This file is part of Bitsquare.
 *
 * Bitsquare is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bitsquare is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bitsquare. If not, see <http://www.gnu.org/licenses/>.
 */

package io.bitsquare.gui.main.settings;

import io.bitsquare.gui.FxmlView;
import io.bitsquare.gui.Navigation;
import io.bitsquare.settings.Preferences;

import javax.inject.Inject;

import viewfx.model.Activatable;
import viewfx.view.View;
import viewfx.view.ViewLoader;
import viewfx.view.support.ActivatableViewAndModel;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;

class SettingsView extends ActivatableViewAndModel<TabPane, Activatable> {

    @FXML Tab preferencesTab, networkSettingsTab;

    private Preferences preferences;
    private Navigation.Listener navigationListener;
    private ChangeListener<Tab> tabChangeListener;

    private final ViewLoader viewLoader;
    private final Navigation navigation;

    @Inject
    public SettingsView(ViewLoader viewLoader, Navigation navigation, Preferences preferences) {
        this.viewLoader = viewLoader;
        this.navigation = navigation;
        this.preferences = preferences;
    }

    @Override
    public void initialize() {
        navigationListener = navigationItems -> {
            if (navigationItems != null && navigationItems.length == 3
                    && navigationItems[1] == FxmlView.SETTINGS)
                loadView(navigationItems[2]);
        };

        tabChangeListener = (ov, oldValue, newValue) -> {
            if (newValue == preferencesTab)
                navigation.navigateTo(FxmlView.MAIN, FxmlView.SETTINGS,
                        FxmlView.PREFERENCES);
            else if (newValue == networkSettingsTab)
                navigation.navigateTo(FxmlView.MAIN, FxmlView.SETTINGS,
                        FxmlView.NETWORK_SETTINGS);
        };
    }

    @Override
    public void doActivate() {
        root.getSelectionModel().selectedItemProperty().addListener(tabChangeListener);
        navigation.addListener(navigationListener);

        if (root.getSelectionModel().getSelectedItem() == preferencesTab)
            navigation.navigateTo(FxmlView.MAIN,
                    FxmlView.SETTINGS,
                    FxmlView.PREFERENCES);
        else
            navigation.navigateTo(FxmlView.MAIN,
                    FxmlView.SETTINGS,
                    FxmlView.NETWORK_SETTINGS);
    }

    @Override
    public void doDeactivate() {
        root.getSelectionModel().selectedItemProperty().removeListener(tabChangeListener);
        navigation.removeListener(navigationListener);
    }

    private void loadView(FxmlView navigationItem) {
        View view = viewLoader.load(navigationItem.getLocation());
        final Tab tab;
        switch (navigationItem) {
            case PREFERENCES:
                tab = preferencesTab;
                break;
            case NETWORK_SETTINGS:
                tab = networkSettingsTab;
                break;
            default:
                throw new IllegalArgumentException("navigation item of type " + navigationItem + " is not allowed");
        }
        tab.setContent(view.getRoot());
        root.getSelectionModel().select(tab);
    }
}

