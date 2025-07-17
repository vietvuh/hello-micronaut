package vvu.centrauthz.domains.resources.models;

import io.micronaut.json.JsonMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import vvu.centrauthz.utilities.JsonTools;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class ResourceTest {

    @Inject
    JsonMapper jsonMapper;

    @Nested
    @DisplayName("patch method tests")
    class PatchMethodTests {

        private Resource createTestResource() {

            var map = Map.of("key1", "value1", "key2", "value2");
            var node = JsonTools.jsonContext(() -> jsonMapper.writeValueToTree(map));

            return Resource.builder()
                    .id(UUID.randomUUID())
                    .type("document")
                    .ownerId(UUID.randomUUID())
                    .parentId(UUID.randomUUID())
                    .sharedWith(List.of(UUID.randomUUID(), UUID.randomUUID()))
                    .tags(List.of("tag1", "tag2"))
                    .details(node)
                    .createdAt(System.currentTimeMillis())
                    .createdBy(UUID.randomUUID())
                    .updatedAt(System.currentTimeMillis())
                    .updatedBy(UUID.randomUUID())
                    .build();
        }

        @Test
        @DisplayName("should patch ownerId when updatedFields contains ownerId")
        void shouldPatchOwnerIdWhenUpdatedFieldsContainsOwnerId() {
            // Given
            Resource originalResource = createTestResource();
            UUID newOwnerId = UUID.randomUUID();
            
            ResourceForPatch.ResourcePatchData patchData = ResourceForPatch.ResourcePatchData.builder()
                    .ownerId(newOwnerId)
                    .build();
            
            ResourceForPatch patch = ResourceForPatch.builder()
                    .updatedFields(List.of("ownerId"))
                    .data(patchData)
                    .build();

            // When
            Resource patchedResource = originalResource.patch(patch);

            // Then
            assertEquals(newOwnerId, patchedResource.ownerId());
            // Verify other fields remain unchanged
            assertEquals(originalResource.id(), patchedResource.id());
            assertEquals(originalResource.type(), patchedResource.type());
            assertEquals(originalResource.parentId(), patchedResource.parentId());
            assertEquals(originalResource.sharedWith(), patchedResource.sharedWith());
            assertEquals(originalResource.tags(), patchedResource.tags());
            assertEquals(originalResource.details(), patchedResource.details());
        }

        @Test
        @DisplayName("should patch parentId when updatedFields contains parentId")
        void shouldPatchParentIdWhenUpdatedFieldsContainsParentId() {
            // Given
            Resource originalResource = createTestResource();
            UUID newParentId = UUID.randomUUID();
            
            ResourceForPatch.ResourcePatchData patchData = ResourceForPatch.ResourcePatchData.builder()
                    .parentId(newParentId)
                    .build();
            
            ResourceForPatch patch = ResourceForPatch.builder()
                    .updatedFields(List.of("parentId"))
                    .data(patchData)
                    .build();

            // When
            Resource patchedResource = originalResource.patch(patch);

            // Then
            assertEquals(newParentId, patchedResource.parentId());
            // Verify other fields remain unchanged
            assertEquals(originalResource.id(), patchedResource.id());
            assertEquals(originalResource.type(), patchedResource.type());
            assertEquals(originalResource.ownerId(), patchedResource.ownerId());
            assertEquals(originalResource.sharedWith(), patchedResource.sharedWith());
            assertEquals(originalResource.tags(), patchedResource.tags());
            assertEquals(originalResource.details(), patchedResource.details());
        }

        @Test
        @DisplayName("should patch sharedWith when updatedFields contains sharedWith")
        void shouldPatchSharedWithWhenUpdatedFieldsContainsSharedWith() {
            // Given
            Resource originalResource = createTestResource();
            List<UUID> newSharedWith = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
            
            ResourceForPatch.ResourcePatchData patchData = ResourceForPatch.ResourcePatchData.builder()
                    .sharedWith(newSharedWith)
                    .build();
            
            ResourceForPatch patch = ResourceForPatch.builder()
                    .updatedFields(List.of("sharedWith"))
                    .data(patchData)
                    .build();

            // When
            Resource patchedResource = originalResource.patch(patch);

            // Then
            assertEquals(newSharedWith, patchedResource.sharedWith());
            // Verify other fields remain unchanged
            assertEquals(originalResource.id(), patchedResource.id());
            assertEquals(originalResource.type(), patchedResource.type());
            assertEquals(originalResource.ownerId(), patchedResource.ownerId());
            assertEquals(originalResource.parentId(), patchedResource.parentId());
            assertEquals(originalResource.tags(), patchedResource.tags());
            assertEquals(originalResource.details(), patchedResource.details());
        }

        @Test
        @DisplayName("should patch tags when updatedFields contains tags")
        void shouldPatchTagsWhenUpdatedFieldsContainsTags() {
            // Given
            Resource originalResource = createTestResource();
            List<String> newTags = List.of("newTag1", "newTag2", "newTag3");
            
            ResourceForPatch.ResourcePatchData patchData = ResourceForPatch.ResourcePatchData.builder()
                    .tags(newTags)
                    .build();
            
            ResourceForPatch patch = ResourceForPatch.builder()
                    .updatedFields(List.of("tags"))
                    .data(patchData)
                    .build();

            // When
            Resource patchedResource = originalResource.patch(patch);

            // Then
            assertEquals(newTags, patchedResource.tags());
            // Verify other fields remain unchanged
            assertEquals(originalResource.id(), patchedResource.id());
            assertEquals(originalResource.type(), patchedResource.type());
            assertEquals(originalResource.ownerId(), patchedResource.ownerId());
            assertEquals(originalResource.parentId(), patchedResource.parentId());
            assertEquals(originalResource.sharedWith(), patchedResource.sharedWith());
            assertEquals(originalResource.details(), patchedResource.details());
        }

        @Test
        @DisplayName("should patch details when updatedFields contains details")
        void shouldPatchDetailsWhenUpdatedFieldsContainsDetails() {
            // Given
            Resource originalResource = createTestResource();
            Map<String, Object> newDetails = Map.of("newKey1", "newValue1", "newKey2", 42);;
            var node = JsonTools.jsonContext(() -> jsonMapper.writeValueToTree(newDetails));
            
            ResourceForPatch.ResourcePatchData patchData = ResourceForPatch.ResourcePatchData.builder()
                    .details(node)
                    .build();
            
            ResourceForPatch patch = ResourceForPatch.builder()
                    .updatedFields(List.of("details"))
                    .data(patchData)
                    .build();

            // When
            Resource patchedResource = originalResource.patch(patch);
            String detailsSValue = JsonTools.jsonContext(() -> jsonMapper.writeValueAsString(node));
            String patchedDetailsSValue = JsonTools.jsonContext(() -> jsonMapper.writeValueAsString(patchedResource.details()));

            // Then
            assertEquals(detailsSValue, patchedDetailsSValue);
            // Verify other fields remain unchanged
            assertEquals(originalResource.id(), patchedResource.id());
            assertEquals(originalResource.type(), patchedResource.type());
            assertEquals(originalResource.ownerId(), patchedResource.ownerId());
            assertEquals(originalResource.parentId(), patchedResource.parentId());
            assertEquals(originalResource.sharedWith(), patchedResource.sharedWith());
            assertEquals(originalResource.tags(), patchedResource.tags());
        }

        @Test
        @DisplayName("should patch multiple fields when updatedFields contains multiple field names")
        void shouldPatchMultipleFieldsWhenUpdatedFieldsContainsMultipleFieldNames() {
            // Given
            Resource originalResource = createTestResource();
            UUID newOwnerId = UUID.randomUUID();
            List<String> newTags = List.of("multiPatchTag1", "multiPatchTag2");
            Map<String, Object> newDetails = Map.of("multiPatchKey", "multiPatchValue");
            var node = JsonTools.jsonContext(() -> jsonMapper.writeValueToTree(newDetails));
            
            ResourceForPatch.ResourcePatchData patchData = ResourceForPatch.ResourcePatchData.builder()
                    .ownerId(newOwnerId)
                    .tags(newTags)
                    .details(node)
                    .build();
            
            ResourceForPatch patch = ResourceForPatch.builder()
                    .updatedFields(List.of("ownerId", "tags", "details"))
                    .data(patchData)
                    .build();

            // When
            Resource patchedResource = originalResource.patch(patch);
            String detailsSValue = JsonTools.jsonContext(() -> jsonMapper.writeValueAsString(node));
            String patchedDetailsSValue = JsonTools.jsonContext(() -> jsonMapper.writeValueAsString(patchedResource.details()));

            // Then
            assertEquals(newOwnerId, patchedResource.ownerId());
            assertEquals(newTags, patchedResource.tags());
            assertEquals(detailsSValue, patchedDetailsSValue);
            // Verify unchanged fields
            assertEquals(originalResource.id(), patchedResource.id());
            assertEquals(originalResource.type(), patchedResource.type());
            assertEquals(originalResource.parentId(), patchedResource.parentId());
            assertEquals(originalResource.sharedWith(), patchedResource.sharedWith());
        }

        @Test
        @DisplayName("should not patch any fields when updatedFields is empty")
        void shouldNotPatchAnyFieldsWhenUpdatedFieldsIsEmpty() {
            // Given
            Resource originalResource = createTestResource();
            var node = JsonTools.jsonContext(() -> jsonMapper.writeValueToTree(Map.of("shouldNotApply", "value")));
            ResourceForPatch.ResourcePatchData patchData = ResourceForPatch.ResourcePatchData.builder()
                    .ownerId(UUID.randomUUID())
                    .parentId(UUID.randomUUID())
                    .sharedWith(List.of(UUID.randomUUID()))
                    .tags(List.of("shouldNotApply"))
                    .details(node)
                    .build();
            
            ResourceForPatch patch = ResourceForPatch.builder()
                    .updatedFields(List.of()) // Empty list
                    .data(patchData)
                    .build();

            // When
            Resource patchedResource = originalResource.patch(patch);

            // Then - all fields should remain unchanged
            assertEquals(originalResource.id(), patchedResource.id());
            assertEquals(originalResource.type(), patchedResource.type());
            assertEquals(originalResource.ownerId(), patchedResource.ownerId());
            assertEquals(originalResource.parentId(), patchedResource.parentId());
            assertEquals(originalResource.sharedWith(), patchedResource.sharedWith());
            assertEquals(originalResource.tags(), patchedResource.tags());
            assertEquals(originalResource.details(), patchedResource.details());
        }

        @Test
        @DisplayName("should not patch fields when updatedFields contains unrecognized field names")
        void shouldNotPatchFieldsWhenUpdatedFieldsContainsUnrecognizedFieldNames() {
            // Given
            Resource originalResource = createTestResource();
            
            ResourceForPatch.ResourcePatchData patchData = ResourceForPatch.ResourcePatchData.builder()
                    .ownerId(UUID.randomUUID())
                    .build();
            
            ResourceForPatch patch = ResourceForPatch.builder()
                    .updatedFields(List.of("nonExistentField", "anotherNonExistentField"))
                    .data(patchData)
                    .build();

            // When
            Resource patchedResource = originalResource.patch(patch);

            // Then - all fields should remain unchanged
            assertEquals(originalResource.id(), patchedResource.id());
            assertEquals(originalResource.type(), patchedResource.type());
            assertEquals(originalResource.ownerId(), patchedResource.ownerId());
            assertEquals(originalResource.parentId(), patchedResource.parentId());
            assertEquals(originalResource.sharedWith(), patchedResource.sharedWith());
            assertEquals(originalResource.tags(), patchedResource.tags());
            assertEquals(originalResource.details(), patchedResource.details());
        }

        @Test
        @DisplayName("should handle null values in patch data")
        void shouldHandleNullValuesInPatchData() {
            // Given
            Resource originalResource = createTestResource();
            
            ResourceForPatch.ResourcePatchData patchData = ResourceForPatch.ResourcePatchData.builder()
                    .sharedWith(null)
                    .tags(null)
                    .details(null)
                    .build();
            
            ResourceForPatch patch = ResourceForPatch.builder()
                    .updatedFields(List.of("sharedWith", "tags", "details"))
                    .data(patchData)
                    .build();

            // When
            Resource patchedResource = originalResource.patch(patch);

            // Then
            assertNull(patchedResource.sharedWith());
            assertNull(patchedResource.tags());
            assertNull(patchedResource.details());
            // Verify unchanged fields
            assertEquals(originalResource.id(), patchedResource.id());
            assertEquals(originalResource.type(), patchedResource.type());
            assertEquals(originalResource.ownerId(), patchedResource.ownerId());
            assertEquals(originalResource.parentId(), patchedResource.parentId());
        }

        @Test
        @DisplayName("should preserve audit fields during patch")
        void shouldPreserveAuditFieldsDuringPatch() {
            // Given
            Resource originalResource = createTestResource();
            UUID newOwnerId = UUID.randomUUID();
            
            ResourceForPatch.ResourcePatchData patchData = ResourceForPatch.ResourcePatchData.builder()
                    .ownerId(newOwnerId)
                    .build();
            
            ResourceForPatch patch = ResourceForPatch.builder()
                    .updatedFields(List.of("ownerId"))
                    .data(patchData)
                    .build();

            // When
            Resource patchedResource = originalResource.patch(patch);

            // Then - audit fields should be preserved
            assertEquals(originalResource.createdAt(), patchedResource.createdAt());
            assertEquals(originalResource.createdBy(), patchedResource.createdBy());
            assertEquals(originalResource.updatedAt(), patchedResource.updatedAt());
            assertEquals(originalResource.updatedBy(), patchedResource.updatedBy());
        }
    }
}
