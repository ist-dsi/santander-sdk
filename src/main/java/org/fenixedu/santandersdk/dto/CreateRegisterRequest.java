package org.fenixedu.santandersdk.dto;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;

public class CreateRegisterRequest {

    private String username;
    private String name;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getDepartmentAcronym() {
        return departmentAcronym;
    }

    public void setDepartmentAcronym(String departmentAcronym) {
        this.departmentAcronym = departmentAcronym;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(BufferedImage photo) {
        if (photo != null) {
            this.photo = transform(photo);
        }
    }

    public RegisterAction getAction() {
        return action;
    }

    public void setAction(RegisterAction action) {
        this.action = action;
    }

    public PickupAddress getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(PickupAddress pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    private byte[] transform(final BufferedImage image) {
        final BufferedImage adjustedImage = transformZoom(image, 9, 10);
        final BufferedImage avatar = Scalr.resize(adjustedImage, Method.QUALITY, Mode.FIT_EXACT, 180, 200);
        return writeImageAsBytes(avatar, "jpg");
    }

    private BufferedImage transformZoom(final BufferedImage source, int xRatio, int yRatio) {
        int destW, destH;
        BufferedImage finale;
        if ((1.0 * source.getWidth() / source.getHeight()) > (1.0 * xRatio / yRatio)) {
            destH = source.getHeight();
            destW = (int) Math.round((destH * xRatio * 1.0) / (yRatio * 1.0));

            int padding = (int) Math.round((source.getWidth() - destW) / 2.0);
            finale = Scalr.crop(source, padding, 0, destW, destH);
        } else {
            destW = source.getWidth();
            destH = (int) Math.round((destW * yRatio * 1.0) / (xRatio * 1.0));

            int padding = (int) Math.round((source.getHeight() - destH) / 2.0);
            finale = Scalr.crop(source, 0, padding, destW, destH);
        }
        return finale;
    }

    private byte[] writeImageAsBytes(BufferedImage image, String fileFormat) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, fileFormat, out);
            return out.toByteArray();
        } catch (IOException ioe) {
            throw new RuntimeException("Failed transforming image into bytearray");
        }
    }
}
