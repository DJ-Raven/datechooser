package com.raven.datechooser.layout;

import com.formdev.flatlaf.util.UIScale;

import java.awt.*;

public class CellLayout implements LayoutManager {

    private int cellSize = 30;

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
            Dimension size = getSize(parent);
            int width = insets.left + insets.right + size.width;
            int height = insets.top + insets.bottom + size.height;
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
            int y = insets.right;
            int width = parent.getWidth() - (insets.left + insets.right);
            int count = parent.getComponentCount();
            int w = (width / 7);
            int h = UIScale.scale(cellSize);
            int space = (int) ((width / 7f - w) * 7 / 2);
            x += space;
            for (int i = 0; i < count; i++) {
                Component com = parent.getComponent(i);
                com.setBounds(x, y, w, h);
                if ((i + 1) % 7 == 0) {
                    y += h;
                    x = insets.left+space;
                } else {
                    x += w;
                }
            }
        }
    }

    private Dimension getSize(Container parent) {
        int count = parent.getComponentCount();
        int s = UIScale.scale(cellSize);
        int max = 0;
        for (int i = 0; i < count; i++) {
            Component com = parent.getComponent(i);
            max = Math.max(max, Math.max(s, com.getPreferredSize().width));
        }
        int width = max * 7;
        int height = UIScale.scale(cellSize) * (count / 7);
        return new Dimension(width, height);
    }
}
