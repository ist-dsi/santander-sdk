package org.fenixedu.santandersdk.dto;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CreateRegisterRequest {

    private String username;
    private String cardName;
    private String fullName;
    private String role;
    private String campus;
    private String departmentAcronym;
    private byte[] photo;
    private RegisterAction action;
    private PickupAddress pickupAddress;

    public CreateRegisterRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(final String cardName) {
        this.cardName = cardName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(final String role) {
        this.role = role;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(final String campus) {
        this.campus = campus;
    }

    public String getDepartmentAcronym() {
        return departmentAcronym;
    }

    public void setDepartmentAcronym(final String departmentAcronym) {
        this.departmentAcronym = departmentAcronym;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(final BufferedImage photo) {
        if (photo != null) {
            this.photo = transform(photo);
        }
    }

    public RegisterAction getAction() {
        return action;
    }

    public void setAction(final RegisterAction action) {
        this.action = action;
    }

    public PickupAddress getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(final PickupAddress pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    private byte[] transform(final BufferedImage image) {
        final BufferedImage adjustedImage = transformZoom(dropAlphaChannel(image), 9, 10);
        final BufferedImage avatar = Scalr.resize(adjustedImage, Method.QUALITY, Mode.FIT_EXACT, 180, 200);
        return writeImageAsBytes(avatar);
    }

    public static BufferedImage dropAlphaChannel(final BufferedImage src) {
        if (src.getColorModel().hasAlpha()) {
            BufferedImage convertedImg = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
            convertedImg.getGraphics().drawImage(src, 0, 0, null);
            return convertedImg;
        }
        return src;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    private BufferedImage transformZoom(final BufferedImage source, final int xRatio, final int yRatio) {
        final int destW, destH;
        final BufferedImage finale;
        if ((1.0 * source.getWidth() / source.getHeight()) > (1.0 * xRatio / yRatio)) {
            destH = source.getHeight();
            destW = (int) Math.round((destH * xRatio * 1.0) / (yRatio * 1.0));

            final int padding = (int) Math.round((source.getWidth() - destW) / 2.0);
            finale = Scalr.crop(source, padding, 0, destW, destH);
        } else {
            destW = source.getWidth();
            destH = (int) Math.round((destW * yRatio * 1.0) / (xRatio * 1.0));

            final int padding = (int) Math.round((source.getHeight() - destH) / 2.0);
            finale = Scalr.crop(source, 0, padding, destW, destH);
        }
        return finale;
    }

    private byte[] writeImageAsBytes(final BufferedImage image) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", out);
            return out.toByteArray();
        } catch (final IOException ignored) {
            throw new RuntimeException("Failed transforming image into bytearray");
        }
    }
}
