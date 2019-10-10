package contracts.person

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should not create an already existing person"

    request {
        url "/person"
        method POST()
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
        status BAD_REQUEST()
        headers {
            contentType applicationJson()
        }
        body([
                code: 'BAD_REQUEST',
                message : $(producer(execute('contains($it, "person already exists")')))
        ])
    }
}
