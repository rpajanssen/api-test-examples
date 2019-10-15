package contracts.person.mocked

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should not update a person with invalid data"

    request {
        url "/api/person"
        method PUT()
        headers {
            contentType applicationJson()
        }
        body (
                id: 25,
                firstName: "",
                lastName: "Hacker"
        )
    }

    response {
        status BAD_REQUEST()
        headers {
            contentType applicationJson()
        }
        body([
                code: '0020',
                data: [
                        id: 25,
                        firstName: '',
                        lastName: 'Hacker'
                ]
        ])
    }
}
