
package cli.models;

import cli.models.menu.Menu;

public abstract class CLI {

        protected final Menu menu;

        public CLI() {
            menu = createMenu();
        }

        protected abstract Menu createMenu();

        protected abstract void addOptions();

        public boolean start() {
            return menu.run();
        }

}
