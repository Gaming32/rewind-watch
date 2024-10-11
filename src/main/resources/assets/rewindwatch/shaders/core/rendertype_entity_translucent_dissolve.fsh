#version 150

#moj_import <rewindwatch:rendertype_entity_translucent_template.glsl>
#moj_import <rewindwatch:dissolve.glsl>

uniform float DissolveOpacity;

vec4 transformColor(vec4 color, vec2 coord) {
    return shouldDissolve(coord, DissolveOpacity) ? vec4(0) : color;
}
