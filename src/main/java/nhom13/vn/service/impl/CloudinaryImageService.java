package nhom13.vn.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.util.Map;

public class CloudinaryImageService {

    private static final long MAX_PROFILE_IMAGE_SIZE = 5L * 1024 * 1024; // 5 MB
    private static final String PROFILE_FOLDER = "XinNghiPhep/profile";
    private static final String HARD_CODED_CLOUDINARY_URL =
            "cloudinary://738942571993247:4-RooJDf3SrUTKS2yFPtbjJqxVI@dwzvtnip3";

    public String uploadProfileImage(Part imagePart, int userId) throws IOException {
        if (imagePart == null || imagePart.getSize() <= 0) {
            throw new IllegalArgumentException("Vui long chon anh de upload");
        }

        if (imagePart.getSize() > MAX_PROFILE_IMAGE_SIZE) {
            throw new IllegalArgumentException("Anh vuot qua gioi han 5MB");
        }

        String contentType = imagePart.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Chi ho tro file anh");
        }

        byte[] imageBytes = imagePart.getInputStream().readAllBytes();
        if (imageBytes.length == 0) {
            throw new IllegalArgumentException("File anh rong");
        }

        Cloudinary cloudinary = buildCloudinary();
        cloudinary.config.secure = true;
        Map uploadResult = cloudinary.uploader().upload(
                imageBytes,
                ObjectUtils.asMap(
                        "resource_type", "image",
                        "folder", PROFILE_FOLDER,
                        "public_id", "user_" + userId + "_" + System.currentTimeMillis(),
                        "overwrite", true
                )
        );

        Object secureUrl = uploadResult.get("secure_url");
        if (secureUrl == null) {
            throw new IOException("Khong nhan duoc URL anh tu Cloudinary");
        }
        return secureUrl.toString();
    }

    private Cloudinary buildCloudinary() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        String cloudinaryUrl = readCredential("CLOUDINARY_URL", dotenv);
        if (!isBlank(cloudinaryUrl)) {
            return new Cloudinary(cloudinaryUrl);
        }

        String cloudName = readCredential("CLOUDINARY_CLOUD_NAME", dotenv);
        String apiKey = readCredential("CLOUDINARY_API_KEY", dotenv);
        String apiSecret = readCredential("CLOUDINARY_API_SECRET", dotenv);

        if (isBlank(cloudName) || isBlank(apiKey) || isBlank(apiSecret)) {
            // Last-resort fallback to keep upload working without server env setup.
            return new Cloudinary(HARD_CODED_CLOUDINARY_URL);
        }

        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    private String readCredential(String key, Dotenv dotenv) {
        String propValue = System.getProperty(key);
        if (!isBlank(propValue)) {
            return propValue;
        }

        String value = System.getenv(key);
        if (!isBlank(value)) {
            return value;
        }
        return dotenv.get(key);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

