import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

public class ContactManagerApp extends JFrame {

    private JTextField nameField;
    private JTextField numberField;
    private JTable contactsTable;
    private DefaultTableModel tableModel;
    private String filePath = "friendsContact.txt";

    private CreateContact createContact = new CreateContact(filePath);
    private ReadContacts readContacts = new ReadContacts(filePath);
    private UpdateContact updateContact = new UpdateContact(filePath);
    private DeleteContact deleteContact = new DeleteContact(filePath);
    //private ExportToExcel exportToExcel = new ExportToExcel();

    public ContactManagerApp() {
        setTitle("Gestor de Contactos");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel de entradas
        JPanel panel = new JPanel();
        nameField = new JTextField(10);
        numberField = new JTextField(10);
        JButton addButton = new JButton("Agregar");
        JButton updateButton = new JButton("Actualizar");
        JButton deleteButton = new JButton("Eliminar");
        JButton exportButton = new JButton("Exportar a Excel");

        panel.add(new JLabel("Nombre:"));
        panel.add(nameField);
        panel.add(new JLabel("Número:"));
        panel.add(numberField);
        panel.add(addButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(exportButton);

        // Tabla
        String[] columns = {"Nombre", "Número"};
        tableModel = new DefaultTableModel(columns, 0);
        contactsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(contactsTable);

        try {
            loadContacts();
        } catch (IOException e) {
            e.printStackTrace();
        }

        addButton.addActionListener(e -> addContact());
        updateButton.addActionListener(e -> updateContact());
        deleteButton.addActionListener(e -> deleteContact());
        //exportButton.addActionListener(e -> exportContacts());

        contactsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = contactsTable.getSelectedRow();
                nameField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                numberField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            }
        });

        add(panel, "North");
        add(scrollPane, "Center");
        setVisible(true);
    }

    private void loadContacts() throws IOException {
        List<String[]> contacts = readContacts.getContacts();
        for (String[] contact : contacts) {
            tableModel.addRow(contact);
        }
    }

    private void addContact() {
        String name = nameField.getText();
        String number = numberField.getText();

        if (name.isEmpty() || number.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Los campos no pueden estar vacíos.");
            return;
        }

        try {
            createContact.addContact(name, number);
            tableModel.addRow(new Object[]{name, number});
        } catch (IOException e) {
            e.printStackTrace();
        }

        clearFields();
    }

    private void updateContact() {
        int selectedRow = contactsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un contacto para actualizar.");
            return;
        }

        String oldName = tableModel.getValueAt(selectedRow, 0).toString();
        String newName = nameField.getText();
        String newNumber = numberField.getText();

        try {
            updateContact.updateContact(oldName, newName, newNumber);
            tableModel.setValueAt(newName, selectedRow, 0);
            tableModel.setValueAt(newNumber, selectedRow, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        clearFields();
    }

    private void deleteContact() {
        int selectedRow = contactsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un contacto para eliminar.");
            return;
        }

        String name = tableModel.getValueAt(selectedRow, 0).toString();

        try {
            deleteContact.deleteContact(name);
            tableModel.removeRow(selectedRow);
        } catch (IOException e) {
            e.printStackTrace();
        }

        clearFields();
    }

    /* private void exportContacts() {
        String excelFilePath = "Contactos.xlsx";
        try {
            exportToExcel.exportContactsToExcel(readContacts.getContacts(), excelFilePath);
            JOptionPane.showMessageDialog(this, "Contactos exportados a " + excelFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */
    private void clearFields() {
        nameField.setText("");
        numberField.setText("");
    }

    public static void main(String[] args) {
        new ContactManagerApp();
    }
}
