package contracts.person

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should update a person"

    request {
        url "/person"
        method PUT()
        headers {
            contentType applicationJson()
        }
        body (
                id: 1,
                firstName: "Jan-Klaas",
                lastName: "Janssen"
        )
    }

    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        body (
                id: 1,
                firstName: "Jan-Klaas",
                lastName: "Janssen"
        )
    }
}
