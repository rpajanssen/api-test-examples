package contracts.person.mocked

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should delete a person"

    request {
        url "/person/2"
        method DELETE()
        headers {
            contentType applicationJson()
        }
    }

    response {
        status NO_CONTENT()
    }
}
