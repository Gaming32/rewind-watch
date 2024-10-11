#version 150

#moj_import <rewindwatch:rendertype_entity_translucent_template.glsl>
#moj_import <rewindwatch:grayscale.glsl>

vec4 transformColor(vec4 color, vec2 coord) {
    return grayscale(color);
}
