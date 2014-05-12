package com.sequenceiq.provisioning;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient;
import com.amazonaws.services.cloudformation.model.CreateStackRequest;
import com.amazonaws.services.cloudformation.model.CreateStackResult;
import com.amazonaws.services.cloudformation.model.ListStacksResult;
import com.amazonaws.services.cloudformation.model.Parameter;

@Controller
public class CloudFormationController {

    @RequestMapping(method = RequestMethod.POST, value = "/listStacks")
    @ResponseBody
    public ResponseEntity<ListStacksResult> listStacks(@RequestBody AwsCredentialsJson awsCredentialsJson) {
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(awsCredentialsJson.getAccessKey(), awsCredentialsJson.getSecretKey());
        AmazonCloudFormationClient amazonCloudFormationClient = new AmazonCloudFormationClient(basicAWSCredentials);
        amazonCloudFormationClient.setRegion(Region.getRegion(Regions.EU_WEST_1));
        ListStacksResult listStacksResult = amazonCloudFormationClient.listStacks();
        return new ResponseEntity<>(listStacksResult, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/stack")
    @ResponseBody
    public ResponseEntity<CreateStackResult> createStack(@RequestBody AwsCredentialsJson awsCredentialsJson) throws UnsupportedEncodingException, IOException {

        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(awsCredentialsJson.getAccessKey(), awsCredentialsJson.getSecretKey());
        AmazonCloudFormationClient amazonCloudFormationClient = new AmazonCloudFormationClient(basicAWSCredentials);
        amazonCloudFormationClient.setRegion(Region.getRegion(Regions.EU_WEST_1));
        CreateStackRequest createStackRequest = new CreateStackRequest()
                .withStackName("testStack")
                .withTemplateBody(readTemplateFromFile("sample-vpc-template"))
                .withParameters(new Parameter().withParameterKey("KeyName").withParameterValue("sequence-eu"));
        CreateStackResult createStackResult = amazonCloudFormationClient.createStack(createStackRequest);
        return new ResponseEntity<>(createStackResult, HttpStatus.OK);
    }

    private String readTemplateFromFile(String templateName) throws UnsupportedEncodingException, IOException {
        StringBuffer sb = new StringBuffer();
        BufferedReader br;
        br = new BufferedReader(new InputStreamReader(new ClassPathResource(templateName).getInputStream(), "UTF-8"));
        for (int c = br.read(); c != -1; c = br.read()) {
            sb.append((char) c);
        }
        return sb.toString();

    }

}