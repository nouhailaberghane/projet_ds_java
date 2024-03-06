package p;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RescaleOp;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class AppImageFilter extends JFrame {
    private static final long serialVersionUID = 1L;
    private JLabel imageLabel;
    private BufferedImage originalImage;
    private BufferedImage filteredImage;
    private JFileChooser fileChooser;

    public AppImageFilter() {
        // Set up the JFrame
        setTitle("Image Filter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create the file chooser
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "jpeg", "png"));

        // Create the panel for the buttons and sliders
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
        JButton openButton = new JButton("Open");
        JButton resetButton = new JButton("Reset");
        JButton grayScaleButton = new JButton("Grayscale");
        JButton invertButton = new JButton("Invert");
        JButton blurButton = new JButton("Blur");
        JButton downloadButton = new JButton("Download");
        JSlider brightnessSlider = new JSlider(-255, 255, 0);
        JSlider contrastSlider = new JSlider(-100, 100, 0);

        // Add the buttons and sliders to the panel
        buttonPanel.add(openButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(grayScaleButton);
        buttonPanel.add(invertButton);
        buttonPanel.add(blurButton);
        buttonPanel.add(new JLabel("Brightness"));
        buttonPanel.add(brightnessSlider);
        buttonPanel.add(new JLabel("Contrast"));
        buttonPanel.add(contrastSlider);
        buttonPanel.add(downloadButton); // Add the download button

        // Create the label for the image
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);

        // Add the panels to the JFrame
        add(buttonPanel, BorderLayout.WEST);
        add(imageLabel, BorderLayout.CENTER);

        // Add listeners to the buttons and sliders
        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showOpenDialog(AppImageFilter.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        originalImage = ImageIO.read(file);
                        filteredImage = originalImage;
                        imageLabel.setIcon(new ImageIcon(originalImage));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        // Add action listener for reset button
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Reset the image to the original
                filteredImage = originalImage;
                imageLabel.setIcon(new ImageIcon(originalImage));
                brightnessSlider.setValue(0);
                contrastSlider.setValue(0);
            }
        });

        // Add action listener for grayscale button
        grayScaleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Convert the image to grayscale
                filteredImage = applyGrayScaleFilter(originalImage);
                imageLabel.setIcon(new ImageIcon(filteredImage));
            }

            private BufferedImage applyGrayScaleFilter(BufferedImage originalImage) {
                BufferedImage grayImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
                Graphics g = grayImage.getGraphics();
                g.drawImage(originalImage, 0, 0, null);
                g.dispose();
                return grayImage;
            }
        });

        // Add action listener for invert button
        invertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Invert the colors of the image
                filteredImage = applyInvertFilter(originalImage);
                imageLabel.setIcon(new ImageIcon(filteredImage));
            }

            private BufferedImage applyInvertFilter(BufferedImage originalImage) {
                BufferedImage invertedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                for (int y = 0; y < originalImage.getHeight(); y++) {
                    for (int x = 0; x < originalImage.getWidth(); x++) {
                        int rgba = originalImage.getRGB(x, y);
                        Color color = new Color(rgba, true);
                        color = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
                        invertedImage.setRGB(x, y, color.getRGB());
                    }
                }
                return invertedImage;
            }
        });

        // Add action listener for blur button
        blurButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Apply blur filter to the image
                filteredImage = applyBlurFilter(originalImage);
                imageLabel.setIcon(new ImageIcon(filteredImage));
            }

            private BufferedImage applyBlurFilter(BufferedImage originalImage) {
                float[] blurMatrix = {
                        0.0625f, 0.125f, 0.0625f,
                        0.125f, 0.25f, 0.125f,
                        0.0625f, 0.125f, 0.0625f
                };
                Kernel blurKernel = new Kernel(3, 3, blurMatrix);
                ConvolveOp blurOp = new ConvolveOp(blurKernel);
                return blurOp.filter(originalImage, null);
            }
        });

        // Add action listener for download button
        downloadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (filteredImage != null) {
                    // Prompt user to choose save location
                    int returnVal = fileChooser.showSaveDialog(AppImageFilter.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        try {
                            // Write the filtered image to the chosen file location
                            ImageIO.write(filteredImage, "png", file);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                } else {
                    // Display message if no filtered image is available
                    JOptionPane.showMessageDialog(AppImageFilter.this, "No filtered image available.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Add listener for brightness slider
        brightnessSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (originalImage != null) {
                    int brightnessValue = brightnessSlider.getValue();
                    filteredImage = adjustBrightness(originalImage, brightnessValue);
                    imageLabel.setIcon(new ImageIcon(filteredImage));
                }
            }
        });

        // Add listener for contrast slider
        contrastSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (originalImage != null) {
                    int contrastValue = contrastSlider.getValue();
                    filteredImage = adjustContrast(originalImage, contrastValue);
                    imageLabel.setIcon(new ImageIcon(filteredImage));
                }
            }
        });
    }

    private BufferedImage adjustBrightness(BufferedImage image, int brightnessValue) {
        RescaleOp op = new RescaleOp(1, brightnessValue, null);
        return op.filter(image, null);
    }

    private BufferedImage adjustContrast(BufferedImage image, float contrastValue) {
        RescaleOp op = new RescaleOp(contrastValue, 0, null);
        return op.filter(image, null);
    }

    public static void main(String[] args) {
        // Create an instance of the AppImageFilter class
        AppImageFilter appImageFilter = new AppImageFilter();

        // Set the visibility of the JFrame to true
        appImageFilter.setVisible(true);
    }
}
