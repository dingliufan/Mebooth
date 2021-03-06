package com.mebooth.mylibrary.main.view;

public interface ToggleView {

    /**
     * Toggle
     */
    void toggle();

    void setOpen(boolean open);

    boolean isOpen();

    void setOnToggleListener(OnToggleListener l);

    interface OnToggleListener {

        /**
         * @param isOpen isOpen
         */
        void onToggle(boolean isOpen);
    }
}
