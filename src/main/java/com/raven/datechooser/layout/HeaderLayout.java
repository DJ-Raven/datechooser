package com.raven.datechooser.layout;

import com.formdev.flatlaf.util.UIScale;

import java.awt.*;

public class HeaderLayout implements LayoutManager {

    private final int gap = 5;
    private final int monthWidth = 110;
    private final int yearWidth = 80;
    private final int buttonWidth = 30;

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int width = insets.left + insets.right + getTotalWidth(parent);
            int height = insets.top + insets.bottom + getMaxHeight(parent);
            return new Dimension(width, height);
        }
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            return new Dimension(0, 0);
        }
    }

    @Override
    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int x = insets.left;
            int y = insets.top;
            int g = UIScale.scale(gap);
            int height = parent.getHeight() - (insets.top + insets.bottom);
            int count = parent.getComponentCount();
            for (int i = 0; i < count; i++) {
                Component com = parent.getComponent(i);
                int w = getWidth(i);
                com.setBounds(x, y, w, height);
                x += (w + g);
            }
        }
    }

    private int getMaxHeight(Container parent) {
        int max = UIScale.scale(30);
        int count = parent.getComponentCount();
        for (int i = 0; i < count; i++) {
            max = Math.max(max, parent.getComponent(i).getPreferredSize().height);
        }
        return max;
    }

    private int getWidth(int index) {
        int width;
        if (index == 1) {
            width = monthWidth;
        } else if (index == 2) {
            width = yearWidth;
        } else {
            width = buttonWidth;
        }
        return UIScale.scale(width);
    }

    private int getTotalWidth(Container parent) {
        int width = (UIScale.scale(buttonWidth) * 2) + UIScale.scale(monthWidth) + UIScale.scale(yearWidth) + (UIScale.scale(gap) * 3);
        return width;
    }
}
