package org.hits.backend.hackathon_tusur.core.affiliate;

import lombok.RequiredArgsConstructor;
import org.hits.backend.hackathon_tusur.client.keycloak.UserClient;
import org.hits.backend.hackathon_tusur.core.user.UserEntity;
import org.hits.backend.hackathon_tusur.core.user.UserRepository;
import org.hits.backend.hackathon_tusur.public_interface.affiliate.AffiliateDto;
import org.hits.backend.hackathon_tusur.public_interface.affiliate.CreateAffiliateDto;
import org.hits.backend.hackathon_tusur.public_interface.affiliate.UpdateAffiliateDto;
import org.hits.backend.hackathon_tusur.public_interface.exception.ExceptionInApplication;
import org.hits.backend.hackathon_tusur.public_interface.exception.ExceptionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AffiliateService {
    private final UserClient userClient;
    private final AffiliateRepository affiliateRepository;
    private final UserRepository userRepository;

    @Transactional
    public String createAffiliate(CreateAffiliateDto dto) {
        if (affiliateRepository.getAffiliateByName(dto.name()).isPresent()) {
            throw new ExceptionInApplication("Affiliate with name " + dto.name() + " already exists", ExceptionType.ALREADY_EXISTS);
        }
        var affiliateEntity = new AffiliateEntity(
                UUID.randomUUID().toString(),
                dto.name(),
                dto.address()
        );

        return affiliateRepository.createAffiliate(affiliateEntity);
    }

    @Transactional
    public void updateAffiliate(UpdateAffiliateDto dto) {
        var affiliate = affiliateRepository.getAffiliateById(dto.id())
                .orElseThrow(() -> new ExceptionInApplication("Affiliate with id " + dto.id() + " not found", ExceptionType.NOT_FOUND));
        var newAffiliate = new AffiliateEntity(
                affiliate.id(),
                dto.name(),
                dto.address()
        );
        affiliateRepository.updateAffiliate(newAffiliate);
    }

    @Transactional
    public void deleteAffiliate(String affiliateId) {
        affiliateRepository.deleteAffiliate(affiliateId);
    }

    @Transactional
    public void assignAffiliateToUser(String affiliateId, String userId) {
        affiliateRepository.getAffiliateById(affiliateId)
                .orElseThrow(() -> new ExceptionInApplication("Affiliate with id " + affiliateId + " not found", ExceptionType.NOT_FOUND));
        var user = userClient.getUser(userId)
                .orElseThrow(() -> new ExceptionInApplication("User with id " + userId + " not found", ExceptionType.NOT_FOUND));
        user = new UserEntity(
                user.id(),
                user.username(),
                user.email(),
                user.password(),
                user.fullName(),
                user.birthDate(),
                Optional.of(affiliateId),
                user.deliveryDateBefore(),
                user.onlineStatus()
        );

        userRepository.updateUser(user);
    }

    @Transactional
    public void unassignAffiliateFromUser(String affiliateId, String userId) {
        affiliateRepository.getAffiliateById(affiliateId)
                .orElseThrow(() -> new ExceptionInApplication("Affiliate with id " + affiliateId + " not found", ExceptionType.NOT_FOUND));
        var user = userClient.getUser(userId)
                .orElseThrow(() -> new ExceptionInApplication("User with id " + userId + " not found", ExceptionType.NOT_FOUND));
        user = new UserEntity(
                user.id(),
                user.username(),
                user.email(),
                user.password(),
                user.fullName(),
                user.birthDate(),
                Optional.empty(),
                user.deliveryDateBefore(),
                user.onlineStatus()
        );

        userRepository.updateUser(user);
    }

    public List<AffiliateDto> getAffiliatesByName(String name) {
        return affiliateRepository.getAffiliatesByName(name)
                .map(affiliateEntity -> new AffiliateDto(
                        affiliateEntity.id(),
                        affiliateEntity.name(),
                        affiliateEntity.address()
                ))
                .toList();
    }

    public Optional<AffiliateDto> getAffiliate(String affiliateId) {
        return affiliateRepository.getAffiliateById(affiliateId)
                .map(affiliateEntity -> new AffiliateDto(
                        affiliateEntity.id(),
                        affiliateEntity.name(),
                        affiliateEntity.address()
                ));
    }
}
