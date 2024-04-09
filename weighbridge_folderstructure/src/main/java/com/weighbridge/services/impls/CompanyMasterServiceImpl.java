package com.weighbridge.services.impls;

import com.weighbridge.dtos.CompanyMasterDto;
import com.weighbridge.entities.CompanyMaster;
import com.weighbridge.exceptions.ResourceNotFoundException;
import com.weighbridge.repsitories.CompanyMasterRepository;
import com.weighbridge.services.CompanyMasterService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyMasterServiceImpl implements CompanyMasterService {

    public final CompanyMasterRepository companyMasterRepository;
    public final ModelMapper modelMapper;
    @Override
    public CompanyMasterDto createCompany(CompanyMasterDto companyMasterDto) {
        CompanyMaster company = modelMapper.map(companyMasterDto, CompanyMaster.class);

        CompanyMaster savedCompany = companyMasterRepository.save(company);

        return modelMapper.map(savedCompany, CompanyMasterDto.class);
    }

    @Override
    public List<CompanyMasterDto> getAllCompany() {
        List<CompanyMaster> companies = companyMasterRepository.findAll();
        return companies.stream().map(company -> modelMapper.map(company, CompanyMasterDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<String> getAllCompanyNameOnly() {

        try{
            List<String> allCompanyListName = companyMasterRepository.findAllCompanyListName();
            return allCompanyListName;
        }
        catch (Exception e){
            throw new ResourceNotFoundException("Failed to retrieve roles");
        }

    }
}
