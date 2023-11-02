package com.raven.datechooser;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.UIScale;
import com.raven.datechooser.layout.CellLayout;
import com.raven.datechooser.layout.HeaderLayout;
import com.raven.datechooser.listener.DateChooserAction;
import com.raven.datechooser.listener.DateChooserListener;
import com.raven.datechooser.render.DateChooserRender;
import com.raven.datechooser.render.DefaultDateChooserRender;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateChooser extends JPanel {
    private final List<DateChooserListener> events = new ArrayList<>();

    private DateSelectable dateSelectable;
    private RDate selectedDate;
    private boolean closePopupAfterSelected = true;
    private RDate[] selectedDateBetween = new RDate[2];
    private int selectedCount = 0;
    private Color themeColor = new Color(67, 127, 251);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private DateSelectionMode dateSelectionMode = DateSelectionMode.SINGLE_DATE_SELECTED;
    private JPopupMenu popup;
    private JTextField textField;
    private JButton labelCurrentDate;
    private String betweenCharacter = " to ";
    private DateChooserRender dateChooserRender = new DefaultDateChooserRender();
    private JSpinner spMonth;
    private JSpinner spYear;
    private JPanel panelHeader;
    private JPanel panelTitle;
    private JPanel panelDate;

    private String[] months = getListMonth();
    private String[] days = getListDay();

    private boolean lightWeightPopupEnabled = true;

    public DateChooser() {
        init();
    }

    private String[] getListMonth() {
        String months[] = (String[]) UIManager.get("DateChooser.listMonth");
        if (months == null) {
            return new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        } else {
            return months;
        }
    }

    private String[] getListDay() {
        String days[] = (String[]) UIManager.get("DateChooser.listDay");
        if (days == null) {
            return new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fir", "Sat"};
        } else {
            return days;
        }
    }


    public boolean isClosePopupAfterSelected() {
        return closePopupAfterSelected;
    }

    public void setClosePopupAfterSelected(boolean closePopupAfterSelected) {
        this.closePopupAfterSelected = closePopupAfterSelected;
    }

    public boolean isLightWeightPopupEnabled() {
        return lightWeightPopupEnabled;
    }

    public void setLightWeightPopupEnabled(boolean lightWeightPopupEnabled) {
        this.lightWeightPopupEnabled = lightWeightPopupEnabled;
        if (popup != null) {
            popup.setLightWeightPopupEnabled(lightWeightPopupEnabled);
        }
    }

    public void setDateSelectable(DateSelectable dateSelectable) {
        this.dateSelectable = dateSelectable;
        for (Component com : panelDate.getComponents()) {
            if (com instanceof ButtonDate) {
                ButtonDate cmdDate = (ButtonDate) com;
                cmdDate.setEnabled(isDateSelectable(cmdDate.getDate().toDate()));
            }
        }
    }

    public Color getThemeColor() {
        return themeColor;
    }

    public void setThemeColor(Color themeColor) {
        this.themeColor = themeColor;
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(SimpleDateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getBetweenCharacter() {
        return betweenCharacter;
    }

    public void setBetweenCharacter(String betweenCharacter) {
        this.betweenCharacter = betweenCharacter;
    }

    public DateSelectionMode getDateSelectionMode() {
        return dateSelectionMode;
    }

    public void setDateSelectionMode(DateSelectionMode dateSelectionMode) {
        this.dateSelectionMode = dateSelectionMode;
    }

    public DateChooserRender getDateChooserRender() {
        return dateChooserRender;
    }

    public void setDateChooserRender(DateChooserRender dateChooserRender) {
        this.dateChooserRender = dateChooserRender;
        showDate();
    }

    public void addActionDateChooserListener(DateChooserListener event) {
        events.add(event);
    }

    public void setTextField(JTextField textField) {
        this.textField = textField;
        textField.setEditable(false);
        textField.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent mouseEvent) {
                        if (textField.isEnabled()) {
                            if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
                                showPopup();
                            }
                        }
                    }
                });
        displayDate();
    }

    public void toDay() {
        setSelectedDate(new Date());
    }

    public void setSelectedDate(int day, int month, int year) {
        setSelectedDate(new RDate(day, month, year));
    }

    public void setSelectedDateBetween(
            int fromDay, int fromMonth, int fromYear, int toDay, int toMonth, int toYear) {
        setSelectedDateBetween(
                new RDate(fromDay, fromMonth, fromYear), new RDate(toDay, toMonth, toYear), false);
    }

    public void setSelectedDateBetween(
            int fromDay,
            int fromMonth,
            int fromYear,
            int toDay,
            int toMonth,
            int toYear,
            boolean displayLast) {
        setSelectedDateBetween(
                new RDate(fromDay, fromMonth, fromYear), new RDate(toDay, toMonth, toYear), displayLast);
    }

    public void setSelectedDateBetween(DateBetween dateBetween, boolean displayLast) {
        setSelectedDateBetween(
                new RDate(dateBetween.getFromDate()), new RDate(dateBetween.getToDate()), displayLast);
    }

    public boolean isDateSelected() {
        boolean selected = false;
        if (dateSelectionMode == DateSelectionMode.SINGLE_DATE_SELECTED) {
            if (selectedDate != null) {
                selected = true;
            }
        } else {
            if (selectedCount == 2) {
                selected = true;
            }
        }
        return selected;
    }

    public void clearDate() {
        selectedDate = null;
        selectedCount = 0;
        selectedDateBetween[0] = null;
        selectedDateBetween[1] = null;
        displayDate();
    }

    public void showPopup() {
        if (popup == null) {
            popup = new JPopupMenu();
            popup.putClientProperty(FlatClientProperties.STYLE, "" +
                    "borderInsets:1,1,1,1");
            popup.setLightWeightPopupEnabled(lightWeightPopupEnabled);
            popup.add(this);
            popup.addPopupMenuListener(new PopupMenuListener() {
                @Override
                public void popupMenuWillBecomeVisible(PopupMenuEvent popupMenuEvent) {
                }

                @Override
                public void popupMenuWillBecomeInvisible(PopupMenuEvent popupMenuEvent) {
                    for (Component component : panelDate.getComponents()) {
                        if (component instanceof ButtonDate) {
                            ((ButtonDate) component).clearHover();
                        }
                    }
                }

                @Override
                public void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {
                }
            });
        }
        SwingUtilities.updateComponentTreeUI(popup);
        popup.show(textField, 0, textField.getHeight());
    }

    public void setLabelCurrentDayVisible(boolean show) {
        labelCurrentDate.setVisible(show);
    }

    public void hidePopup() {
        if (popup != null) {
            popup.setVisible(false);
        }
    }

    private void closePopup() {
        if ((popup != null && popup.isVisible()) && isClosePopupAfterSelected()) {
            popup.setVisible(false);
        }
    }

    public Date getSelectedDate() throws DateChooserException {
        if (dateSelectionMode == DateSelectionMode.BETWEEN_DATE_SELECTED) {
            if (selectedCount == 2) {
                return getSelectedDateBetween().getFromDate();
            } else {
                return selectedDateBetween[0].toDate();
            }
        }
        return selectedDate.toDate();
    }

    public void setSelectedDate(Date date) {
        setSelectedDate(new RDate(date));
    }

    private void setSelectedDate(RDate date) throws DateChooserException {
        if (ErrorCheck.checkDate(date)) {
            selectedDateBetween[0] = date.copy();
            selectedDateBetween[1] = date.copy();
            selectedCount = 2;
            this.selectedDate = date;
            repaint();
            displayDate(date);
            displayDate();
            if (dateSelectionMode == DateSelectionMode.SINGLE_DATE_SELECTED) {
                runEventDateChanged(new DateChooserAction(DateChooserAction.METHOD_SET));
            } else {
                runEventDateBetweenChanged(new DateChooserAction(DateChooserAction.METHOD_SET));
            }
            closePopup();
        }
    }

    public DateBetween getSelectedDateBetween() throws DateChooserException {
        if (dateSelectionMode == DateSelectionMode.SINGLE_DATE_SELECTED) {
            throw new DateChooserException("Date chooser is current SINGLE_DATE_SELECTED mode");
        }
        if (selectedCount != 2) {
            throw new DateChooserException("Date between not selected");
        }
        DateBetween dateBetween = new DateBetween(selectedDateBetween[0].toDate(), selectedDateBetween[1].toDate());
        dateBetween.fixDate();
        return dateBetween;
    }

    public void setSelectedDateBetween(DateBetween dateBetween) {
        setSelectedDateBetween(
                new RDate(dateBetween.getFromDate()), new RDate(dateBetween.getToDate()), false);
    }

    private void displayDate(RDate date) {
        spMonth.setValue(indexToMonth(date.getMonth() - 1));
        spYear.setValue(date.getYear());
    }

    private boolean isDateSelectable(Date date) {
        return dateSelectable == null || dateSelectable.isDateSelectable(date);
    }

    private void setSelectedDateBetween(RDate fromDate, RDate toDate, boolean displayLast) {
        if (dateSelectionMode == DateSelectionMode.SINGLE_DATE_SELECTED) {
            throw new DateChooserException("Date chooser is current SINGLE_DATE_SELECTED mode");
        } else {
            if (ErrorCheck.checkDate(fromDate) && ErrorCheck.checkDate(toDate)) {
                selectedDateBetween[0] = fromDate;
                selectedDateBetween[1] = toDate;
                selectedCount = 2;
                if (displayLast) {
                    displayDate(toDate);
                } else {
                    displayDate(fromDate);
                }
                displayDate();
                runEventDateBetweenChanged(new DateChooserAction(DateChooserAction.METHOD_SET));
            }
        }
    }

    private synchronized void runEventDateChanged(DateChooserAction action) {
        for (DateChooserListener event : events) {
            event.dateChanged(getSelectedDate(), action);
        }
    }

    private synchronized void runEventDateBetweenChanged(DateChooserAction action) {
        for (DateChooserListener event : events) {
            event.dateBetweenChanged(getSelectedDateBetween(), action);
        }
    }

    private void init() {
        putClientProperty(FlatClientProperties.STYLE, "" +
                "background:if($DateChooser.background,$DateChooser.background,$Panel.background);" +
                "border:5,5,5,5");
        setLayout(new DateChooserLayout());
        Color focusColor = UIManager.getColor("Component.focusColor");
        if (focusColor != null) {
            themeColor = focusColor;
        }
        createHeader();
        createDate();
        createLabelCurrentDate();
        RDate now = new RDate();
        spMonth.setValue(indexToMonth(now.getMonth() - 1));
        initEvent();
        spYear.setValue(now.getYear());
        toDay();
    }

    private void initEvent() {
        spYear.addChangeListener((ChangeEvent e) -> showDate());
        spMonth.addChangeListener((ChangeEvent e) -> showDate());
    }

    private void createHeader() {
        panelHeader = new JPanel(new HeaderLayout());
        panelHeader.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:if($DateChooser.background,$DateChooser.background,$Panel.background)");
        JButton cmdBack = new JButton(new FlatSVGIcon("com/raven/datechooser/icon/arrowCollapse.svg"));
        JButton cmdNext = new JButton(new FlatSVGIcon("com/raven/datechooser/icon/arrowExpand.svg"));
        spMonth = new JSpinner();
        spYear = new JSpinner();
        JSpinner.NumberEditor numberEditor = (JSpinner.NumberEditor) spYear.getEditor();
        numberEditor.getFormat().applyPattern("#0");
        AbstractDocument doc =
                new PlainDocument() {
                    @Override
                    public void setDocumentFilter(DocumentFilter filter) {
                        if (filter instanceof DateChooserEditorDocumentFilter) {
                            super.setDocumentFilter(filter);
                        }
                    }
                };
        cmdBack.addActionListener(
                (ActionEvent e) -> {
                    int month = monthToIndex(spMonth.getValue().toString());
                    if (month == 0) {
                        spMonth.setValue(indexToMonth(11));
                        int year = Integer.parseInt(spYear.getValue().toString());
                        spYear.setValue(year - 1);
                    } else {
                        spMonth.setValue(indexToMonth(month - 1));
                    }
                });
        cmdNext.addActionListener(
                (ActionEvent e) -> {
                    int month = monthToIndex(spMonth.getValue().toString());
                    if (month == 11) {
                        spMonth.setValue(indexToMonth(0));
                        int year = Integer.parseInt(spYear.getValue().toString());
                        spYear.setValue(year + 1);
                    } else {
                        spMonth.setValue(indexToMonth(month + 1));
                    }
                });
        doc.setDocumentFilter(new DateChooserEditorDocumentFilter());
        numberEditor.getTextField().setDocument(doc);
        spMonth.setModel(new SpinnerListModel(months));
        panelHeader.add(cmdBack);
        panelHeader.add(spMonth);
        panelHeader.add(spYear);
        panelHeader.add(cmdNext);
        panelTitle = new JPanel(new CellLayout());
        panelTitle.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:if($DateChooser.background,$DateChooser.background,$Panel.background)");
        spMonth.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:if($DateChooser.background,$DateChooser.background,$Panel.background);" +
                "buttonBackground:if($DateChooser.background,$DateChooser.background,$Panel.background)");
        spYear.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:if($DateChooser.background,$DateChooser.background,$Panel.background);" +
                "buttonBackground:if($DateChooser.background,$DateChooser.background,$Panel.background)");
        cmdBack.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:if($DateChooser.background,$DateChooser.background,$Panel.background)");
        cmdNext.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:if($DateChooser.background,$DateChooser.background,$Panel.background)");
        panelTitle.add(new JLabel(days[0], JLabel.CENTER));
        panelTitle.add(new JLabel(days[1], JLabel.CENTER));
        panelTitle.add(new JLabel(days[2], JLabel.CENTER));
        panelTitle.add(new JLabel(days[3], JLabel.CENTER));
        panelTitle.add(new JLabel(days[4], JLabel.CENTER));
        panelTitle.add(new JLabel(days[5], JLabel.CENTER));
        panelTitle.add(new JLabel(days[6], JLabel.CENTER));

        add(panelHeader);
        add(panelTitle);
    }

    private void createDate() {
        panelDate = new JPanel(new CellLayout());
        panelDate.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:if($DateChooser.background,$DateChooser.background,$Panel.background)");
        add(panelDate);
    }

    private void createLabelCurrentDate() {
        labelCurrentDate = new JButton(dateChooserRender.renderLabelCurrentDate(this, new Date()));
        labelCurrentDate.setForeground(themeColor);
        labelCurrentDate.setContentAreaFilled(false);
        labelCurrentDate.setBorder(null);
        labelCurrentDate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        labelCurrentDate.addActionListener(e -> {
            if (dateSelectionMode == DateSelectionMode.SINGLE_DATE_SELECTED) {
                toDay();
            } else {
                if (selectedCount == 1) {
                    RDate now = new RDate();
                    int month = monthToIndex(spMonth.getValue().toString()) + 1;
                    int year = Integer.parseInt(spYear.getValue().toString());
                    if (now.getMonth() == month && now.getYear() == year) {
                        selectedDateBetween[1] = now;
                        selectedCount = 2;
                        repaint();
                        displayDate();
                        runEventDateBetweenChanged(new DateChooserAction(DateChooserAction.USER_SELECT));
                        closePopup();
                    } else {
                        displayDate(new RDate());
                    }
                } else {
                    toDay();
                }
            }

        });
        add(labelCurrentDate);
    }

    private void showDate() {
        panelDate.removeAll();
        int month = monthToIndex(spMonth.getValue().toString());
        int year = Integer.parseInt(spYear.getValue().toString());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DATE, 1);
        int startDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.add(Calendar.DATE, -startDay);
        for (int i = 1; i <= 7 * 6; i++) {
            ButtonDate cmd = new ButtonDate(new RDate(calendar));
            cmd.setText(dateChooserRender.renderDateCell(this, calendar.getTime()));
            if (calendar.get(Calendar.MONTH) != month) {
                cmd.putClientProperty(FlatClientProperties.STYLE, "" +
                        "foreground:$Button.disabledText");
            }
            panelDate.add(cmd);
            calendar.add(Calendar.DATE, 1);
        }
        panelDate.revalidate();
    }

    private void displayDate() {
        if (textField != null) {
            if (dateSelectionMode == DateSelectionMode.SINGLE_DATE_SELECTED) {
                if (isDateSelected()) {
                    textField.setText(dateChooserRender.renderTextFieldDate(this, getSelectedDate()));
                } else {
                    textField.setText("");
                }
            } else {
                DateBetween dateBetween = new DateBetween();
                dateBetween.setFromDate(selectedDateBetween[0].toDate());
                if (selectedCount == 2) {
                    dateBetween.setToDate(selectedDateBetween[1].toDate());
                    dateBetween.fixDate();
                }
                if (selectedCount != 0) {
                    textField.setText(dateChooserRender.renderTextFieldDateBetween(this, dateBetween));
                } else {
                    textField.setText("");
                }
            }
        }
    }

    private int monthToIndex(String val) {
        for (int i = 0; i < months.length; i++) {
            if (val.equals(months[i])) {
                return i;
            }
        }
        return -1;
    }

    private String indexToMonth(int index) {
        return months[index];
    }

    public static enum DateSelectionMode {
        SINGLE_DATE_SELECTED,
        BETWEEN_DATE_SELECTED
    }

    public class ButtonDate extends JButton {

        private RDate date;
        private boolean hover = false;
        private boolean selected = false;

        public ButtonDate(RDate date) {
            setEnabled(isDateSelectable(date.toDate()));
            this.date = date;
            setBorder(BorderFactory.createEmptyBorder());
            setContentAreaFilled(false);
            addMouseListener(
                    new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            if (isEnabled()) {
                                hover = true;
                                if (dateSelectionMode == DateSelectionMode.BETWEEN_DATE_SELECTED) {
                                    if (selectedCount == 1) {
                                        selectedDateBetween[1] = date;
                                    }
                                }
                                panelDate.repaint();
                            }
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            if (isEnabled()) {
                                hover = false;
                                if (dateSelectionMode == DateSelectionMode.BETWEEN_DATE_SELECTED) {
                                }
                                panelDate.repaint();
                            }
                        }
                    });
            addActionListener(
                    (ActionEvent e) -> {
                        if (isEnabled()) {
                            if (dateSelectionMode == DateSelectionMode.SINGLE_DATE_SELECTED) {
                                selectedDate = date;
                                panelDate.repaint();
                                displayDate();
                                runEventDateChanged(new DateChooserAction(DateChooserAction.USER_SELECT));
                                closePopup();
                            } else {
                                if (selectedCount >= 2) {
                                    selectedCount = 0;
                                    selectedDateBetween[1] = null;
                                }
                                if (selectedCount == 0) {
                                    selectedDateBetween[0] = date;
                                    selectedCount++;
                                    displayDate();
                                } else if (selectedCount == 1) {
                                    selectedDateBetween[1] = date;
                                    selectedCount++;
                                    displayDate();
                                    runEventDateBetweenChanged(new DateChooserAction(DateChooserAction.USER_SELECT));
                                    closePopup();
                                }
                                panelDate.repaint();
                            }
                        }
                    });
        }

        public RDate getDate() {
            return date;
        }

        public void setDate(RDate date) {
            this.date = date;
        }

        public void clearHover() {
            hover = false;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            selected = false;
            if (dateSelectionMode == DateSelectionMode.SINGLE_DATE_SELECTED) {
                if (selectedDate != null && date.equals(selectedDate)) {
                    g2.setColor(themeColor);
                    g2.fill(createShape(0));
                } else {
                    checkMouseHover(g2);
                }
            } else {
                if (selectedCount > 0) {
                    if (selectedDateBetween[1] != null) {
                        int compare = selectedDateBetween[0].compareTo(selectedDateBetween[1]);
                        if (compare == 0) {
                            if (date.equals(selectedDateBetween[0])) {
                                g2.setColor(themeColor);
                                g2.fill(createShape(selectedCount == 1 ? 1 : 0));
                                selected = true;
                            } else {
                                checkMouseHover(g2);
                            }
                        } else {
                            RDate fromDate;
                            RDate toDate;
                            if (compare == 1) {
                                fromDate = selectedDateBetween[1];
                                toDate = selectedDateBetween[0];
                            } else {
                                fromDate = selectedDateBetween[0];
                                toDate = selectedDateBetween[1];
                            }
                            if (date.equals(fromDate)) {
                                g2.setColor(themeColor);
                                g2.fill(createShape(1));
                                selected = true;
                            } else if (date.equals(toDate)) {
                                g2.setColor(themeColor);
                                g2.fill(createShape(2));
                                selected = true;
                            } else {
                                if (date.isBetweenOf(fromDate, toDate)) {
                                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                                    g2.setColor(themeColor);
                                    g2.fill(createShape(3));
                                } else {
                                    if (selectedCount == 2) {
                                        checkMouseHover(g2);
                                    }
                                }
                            }
                        }
                    } else {
                        if (date.equals(selectedDateBetween[0])) {
                            g2.setColor(themeColor);
                            g2.fill(createShape(1));
                            selected = true;
                        }
                    }
                } else {
                    checkMouseHover(g2);
                }
            }
            g2.dispose();
            super.paintComponent(g);
        }

        private void checkMouseHover(Graphics2D g2) {
            if (hover) {
                Composite oldComposite = g2.getComposite();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                g2.setColor(getForeground());
                int width = getWidth();
                int height = getHeight();
                int size = Math.min(width, height);
                int x = (width - size) / 2;
                int y = (height - size) / 2;
                g2.fill(new Ellipse2D.Double(x, y, size, size));
                g2.setComposite(oldComposite);
            }
        }

        @Override
        public Color getForeground() {
            if (selected) {
                return getContrastColor(themeColor);
            } else {
                return super.getForeground();
            }
        }

        public Color getContrastColor(Color color) {
            double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
            return y >= 128 ? Color.BLACK : Color.WHITE;
        }

        private Shape createShape(int type) {
            double width = getWidth();
            double height = getHeight();
            double size = Math.min(width, height) - 2;
            double x = (width - size) / 2;
            double y = (height - size) / 2;
            Shape shape;
            switch (type) {
                case 1: {
                    Area area = new Area(new Ellipse2D.Double(0, y, size, size));
                    area.add(
                            new Area(new RoundRectangle2D.Double(size / 2, y, width - size / 2, size, 2, 2)));
                    shape = area;
                    break;
                }
                case 2: {
                    Area area = new Area(new Ellipse2D.Double(width - size, y, size, size));
                    area.add(new Area(new RoundRectangle2D.Double(0, y, width - size / 2, size, 2, 2)));
                    shape = area;
                    break;
                }
                case 3: {
                    Area area = new Area(new RoundRectangle2D.Double(0, y, width, size, 2, 2));
                    shape = area;
                    break;
                }
                default: {
                    Area area = new Area(new Ellipse2D.Double(x, y, size, size));
                    shape = area;
                    break;
                }
            }
            return shape;
        }
    }

    private class DateChooserLayout implements LayoutManager {

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
                int width = insets.left + insets.right + panelHeader.getPreferredSize().width;
                int height = insets.top + insets.bottom + (panelHeader.getPreferredSize().height + UIScale.scale(2) + panelTitle.getPreferredSize().height + panelDate.getPreferredSize().height);
                if (labelCurrentDate.isVisible()) {
                    height += labelCurrentDate.getPreferredSize().height + UIScale.scale(3);
                }
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
                panelHeader.setBounds(x, y, width, panelHeader.getPreferredSize().height);
                y += panelHeader.getPreferredSize().height + UIScale.scale(1);
                panelTitle.setBounds(x, y, width, panelTitle.getPreferredSize().height);
                y += panelHeader.getPreferredSize().height + UIScale.scale(1);
                panelDate.setBounds(x, y, width, panelDate.getPreferredSize().height);
                if (labelCurrentDate.isVisible()) {
                    y += panelDate.getPreferredSize().height + UIScale.scale(3);
                    int lbw = labelCurrentDate.getPreferredSize().width;
                    int lx = (width - lbw) / 2;
                    labelCurrentDate.setBounds(lx, y, lbw, labelCurrentDate.getPreferredSize().height);
                }
            }
        }
    }
}
