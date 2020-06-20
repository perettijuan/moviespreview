package com.jpp.mpabout

/**
 * Represents the navigation events that can be routed in the about section.
 */
internal sealed class AboutNavEvent {
    data class InnerNavigation(val url: String) : AboutNavEvent()
    data class OpenGooglePlay(val url: String) : AboutNavEvent()
    data class OpenSharing(val url: String) : AboutNavEvent()
    data class OuterNavigation(val url: String) : AboutNavEvent()
}
