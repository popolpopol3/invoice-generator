package org.example;

import com.lowagie.text.DocumentException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Main{
    private static JTextField dateTextField;
    private static JTextField nameTextField;
    private static JTextField surnameTextField;
    private static JTextField paymentAddressTextField;
    private static JTextField proSiretTextField;
    private static JTextField proTvaTextField;
    private static JTextField shippingAddressTextField;
    private static JTextField tvaPercentTextField;
    private static JTextField taxedTotalTextField;

    private static JTextField paymentDateTextField;
    private static JTextField paymentModeTextField;
    private static JTable productTable;
    private static JFileChooser destinationFolder;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static String parseThymeleafTemplate() throws IOException {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        Context  context = new Context();

        FileHandler fileHandler = new FileHandler();
        int counter = fileHandler.readFile();

        context.setVariable("n_commande", counter);
        context.setVariable("date", dateTextField.getText());
        context.setVariable("prenom", nameTextField.getText());
        context.setVariable("nom", surnameTextField.getText());
        context.setVariable("adresse_facture", paymentAddressTextField.getText());
        context.setVariable("tva_acheteur", proTvaTextField.getText());
        context.setVariable("siret_acheteur", proSiretTextField.getText());
        context.setVariable("adresse_livraison", shippingAddressTextField.getText());
        double tvaPercent = Double.parseDouble(tvaPercentTextField.getText().replace(",", "."));
        double taxedTotal = Double.parseDouble(taxedTotalTextField.getText().replace(",", "."));
        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        context.setVariable("total_ht", decimalFormat.format(taxedTotal / (1 + tvaPercent/100)));
        context.setVariable("tva", decimalFormat.format(tvaPercent));
        context.setVariable("total_tva", decimalFormat.format(taxedTotal * (1 - 1 / (1 + tvaPercent / 100))));
        context.setVariable("total_ttc", decimalFormat.format(taxedTotal));
        context.setVariable("date_paiement", paymentDateTextField.getText());
        context.setVariable("mode_paiement", paymentModeTextField.getText());
        List<Product> products = new ArrayList<>();

        for (int i=0; i < productTable.getModel().getRowCount(); i++) {
            String name = "";
            String quantity = "1";
            for (int j=0; j < productTable.getModel().getColumnCount(); j++) {
                if (j==0)
                    name = (String) productTable.getValueAt(i,j);
                else if (j==1)
                    quantity = (String) productTable.getValueAt(i,j);
            }
            products.add(new Product(name, quantity));
        }
        context.setVariable("prods", products);

        fileHandler.writeFile(counter);

        return templateEngine.process("facture", context);
    }
    private static void generatePdfFromHtml(String html) throws IOException, DocumentException {
        String outputFolder = destinationFolder.getSelectedFile().getPath() + File.separator + "facture " + surnameTextField.getText() + " " + dateTextField.getText().replace("/", "-") + ".pdf";
        OutputStream outputStream = new FileOutputStream(outputFolder);
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);

        outputStream.close();
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Nouvelle facture");

        frame.setBounds(560, 100, 500, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setPreferredSize(new Dimension(500,700));

        frame.getContentPane().add(new JLabel("Entre les informations de ta facture <3"));
        frame.getContentPane().add(new JLabel("Date"));
        dateTextField = new JTextField();
        frame.getContentPane().add(dateTextField);

        frame.getContentPane().add(new JLabel("Prénom"));
        nameTextField = new JTextField();
        frame.getContentPane().add(nameTextField);

        frame.getContentPane().add(new JLabel("Nom"));
        surnameTextField = new JTextField();
        frame.getContentPane().add(surnameTextField);

        frame.getContentPane().add(new JLabel("Addresse de facturation"));
        paymentAddressTextField = new JTextField();
        frame.getContentPane().add(paymentAddressTextField);

        frame.getContentPane().add(new JLabel("Siret Professionnel"));
        proSiretTextField = new JTextField();
        frame.getContentPane().add(proSiretTextField);

        frame.getContentPane().add(new JLabel("TVA Professionnel"));
        proTvaTextField = new JTextField();
        frame.getContentPane().add(proTvaTextField);

        frame.getContentPane().add(new JLabel("Addresse de livraison"));
        shippingAddressTextField = new JTextField();
        frame.getContentPane().add(shippingAddressTextField);

        Object[][] data = {{"Agneau", "1"}};

        DefaultTableModel model = new DefaultTableModel(data, new String[]{"Article", "Quantité"});
        productTable = new JTable(model);
        frame.getContentPane().add(productTable.getTableHeader());
        frame.getContentPane().add(productTable);

        JButton btnPlus = new JButton("+");

        btnPlus.setBackground(Color.PINK);
        btnPlus.addActionListener(e -> model.addRow(new Object[] {"Bélier", "1"}));
        frame.getContentPane().add(btnPlus);

        frame.getContentPane().add(new JLabel("% TVA"));
        tvaPercentTextField = new JTextField();
        frame.getContentPane().add(tvaPercentTextField);

        frame.getContentPane().add(new JLabel("Total TTC"));
        taxedTotalTextField = new JTextField();
        frame.getContentPane().add(taxedTotalTextField);

        frame.getContentPane().add(new JLabel("Date de paiement"));
        paymentDateTextField = new JTextField();
        frame.getContentPane().add(paymentDateTextField);

        frame.getContentPane().add(new JLabel("Mode de paiement"));
        paymentModeTextField = new JTextField();
        frame.getContentPane().add(paymentModeTextField);

        JButton targetFolder = new JButton("Enregistrer sous");
        targetFolder.setBackground(Color.CYAN);
        targetFolder.addActionListener( e -> {
            destinationFolder = new JFileChooser();
            destinationFolder.setCurrentDirectory(new File("."));
            destinationFolder.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            destinationFolder.setAcceptAllFileFilterUsed(false);
            destinationFolder.showSaveDialog(null);
        });
        frame.getContentPane().add(targetFolder);

        JButton btnSubmit = new JButton("Générer PDF");

        btnSubmit.setBackground(Color.PINK);
        btnSubmit.addActionListener(e -> {
            try {
                generatePdfFromHtml(parseThymeleafTemplate());
            } catch (IOException | DocumentException ex) {
                throw new RuntimeException(ex);
            }
            frame.dispose();
        });
        frame.getContentPane().add(btnSubmit);

        frame.pack();
        frame.setVisible(true);
    }
}