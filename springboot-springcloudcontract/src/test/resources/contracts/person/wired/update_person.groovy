package contracts.person

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should update a person"

    request {
        url "/api/person"
        method PUT()
        headers {
            contentType applicationJson()
        }
        body (
                id: 3,
                firstName: "Erik",
                lastName: "Erikson"
        )
    }

    response {
        status OK()
    }
}
