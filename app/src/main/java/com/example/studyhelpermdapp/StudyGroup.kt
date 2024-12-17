package com.example.studyhelpermdapp

/**
 * StudyGroup:
 * A data class representing a study group.
 * This class is used to store and retrieve study group information from Firebase.
 *
 * @property id Unique identifier for the study group.
 * @property name Name of the study group.
 * @property location Location of the study group (e.g., coordinates or a description).
 * @property members List of user IDs representing members of the study group.
 */
data class StudyGroup(
    val id: String = "",             // Default value: Empty string for group ID
    val name: String = "",           // Default value: Empty string for group name
    val location: String = "",       // Default value: Empty string for group location
    val members: List<String> = emptyList() // Default value: Empty list for group members
)
