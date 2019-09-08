package legends.views

import com.fasterxml.jackson.annotation.JsonProperty
import legends.models.ImageModel

class ImageView(
        @JsonProperty("img_name") val imageName: String,
        @JsonProperty("location") val nginxPath: String
) {
    constructor(image: ImageModel) : this(
            imageName = image.imageName,
            nginxPath = image.nginxPath
    )
}